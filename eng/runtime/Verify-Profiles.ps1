param(
    [string] $ProfileRecord
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$repository = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..\..')).Path
$profilesRoot = (Resolve-Path -LiteralPath (Join-Path $repository 'run\profiles')).Path

function Resolve-RepositoryRelativePath([string] $RelativePath) {
    if ([IO.Path]::IsPathRooted($RelativePath)) { throw "Repository path must be relative: $RelativePath" }
    $resolved = [IO.Path]::GetFullPath((Join-Path $repository $RelativePath))
    if (-not $resolved.StartsWith($repository + '\', [StringComparison]::OrdinalIgnoreCase)) {
        throw "Repository path escaped the repository: $RelativePath"
    }
    return $resolved
}

function Get-Sha512([string] $Path) {
    return (Get-FileHash -LiteralPath $Path -Algorithm SHA512).Hash.ToLowerInvariant()
}

$artifactHashes = @{
    'CustomSkinLoader_Universal-15.0.1.jar' = '8c65193c46c1435ddea571901f1dd04cc179179f59f1c1a0449349de04722b9c1b6e5efaec166975e05d2fa0b05f6c07c9d5d93a05f1c9a57fece2a0d0fc1ce7'
    'modmenu-20.0.1.jar' = '1aa297ab5e6fac71ad6af750fe6f8cd25281bd00c0340e5fe1c1e9d153d1198df03be1df55727a4a2116db97dcaad541d41754f3fc0063abfe4345b60f193fb4'
}
$testModFiles = @{
    'CustomSkinLoader:15.0.1-Universal' = 'CustomSkinLoader_Universal-15.0.1.jar'
    'ModMenu:20.0.1' = 'modmenu-20.0.1.jar'
}
$knownTestModFiles = @('CustomSkinLoader_Universal-15.0.1.jar', 'modmenu-20.0.1.jar', 'zume-1.2.2.jar')
$requiredFields = @('schemaVersion', 'node', 'profile', 'role', 'playerName', 'resolution', 'gameDirectory', 'modsDirectory', 'requiredJvmArgs', 'requiredTestMods', 'compatibilityFixture')
$nodes = @('1.20.1-forge', '1.21.1-neoforge', '26.1.2-neoforge', '26.2-fabric', '26.2-neoforge')
$definitions = @(
    @{ Profile = 'S1'; Role = 'PRIMARY'; Side = 'client/primary' },
    @{ Profile = 'S2'; Role = 'PRIMARY'; Side = 'client/primary' },
    @{ Profile = 'M1'; Role = 'PRIMARY'; Side = 'client/primary' },
    @{ Profile = 'M1'; Role = 'SECONDARY'; Side = 'client/secondary' },
    @{ Profile = 'M2'; Role = 'PRIMARY'; Side = 'client/primary' },
    @{ Profile = 'M2'; Role = 'SECONDARY'; Side = 'client/secondary' }
)

$expectedRecords = foreach ($node in $nodes) {
    foreach ($definition in $definitions) {
        Join-Path $profilesRoot "$node\$($definition.Side.Replace('/', '\'))\$($definition.Profile.ToLowerInvariant()).json"
    }
}
$actualRecords = @(Get-ChildItem -LiteralPath $profilesRoot -File -Recurse -Filter '*.json' | Select-Object -ExpandProperty FullName)
if ($actualRecords.Count -ne 30) { throw "Expected 30 repository profile records, found $($actualRecords.Count)." }
if (@(Compare-Object ($expectedRecords | Sort-Object) ($actualRecords | Sort-Object)).Count -ne 0) {
    throw 'Repository profile record set differs from the authorized matrix.'
}

$recordsToVerify = if ($ProfileRecord) { @((Resolve-Path -LiteralPath $ProfileRecord).Path) } else { $actualRecords }
$verifiedInstances = 0
foreach ($recordPath in $recordsToVerify) {
    if (-not $recordPath.StartsWith($profilesRoot + '\', [StringComparison]::OrdinalIgnoreCase)) {
        throw "Profile record is outside the repository profile root: $recordPath"
    }
    $record = Get-Content -LiteralPath $recordPath -Raw | ConvertFrom-Json
    foreach ($field in $requiredFields) {
        if ($field -notin @($record.PSObject.Properties.Name)) { throw "Profile record is missing $field`: $recordPath" }
    }
    if ([int] $record.schemaVersion -ne 1) { throw "Unsupported profile schema: $recordPath" }
    if ($record.node -notin $nodes) { throw "Unauthorized profile node: $recordPath" }
    $definition = $definitions | Where-Object { $_.Profile -eq $record.profile -and $_.Role -eq $record.role } | Select-Object -First 1
    if (-not $definition) { throw "Unauthorized profile/role: $recordPath" }

    $expectedGameDirectory = "run/instances/$($record.node)/$($definition.Side)/$($record.profile.ToLowerInvariant())"
    $expectedModsDirectory = "$expectedGameDirectory/mods"
    if ($record.gameDirectory -ne $expectedGameDirectory -or $record.modsDirectory -ne $expectedModsDirectory) {
        throw "Profile repository-relative directory mismatch: $recordPath"
    }
    $gameDirectory = Resolve-RepositoryRelativePath ([string] $record.gameDirectory)
    $modsDirectory = Resolve-RepositoryRelativePath ([string] $record.modsDirectory)
    $expectedName = if ($record.role -eq 'PRIMARY') { 'RecRivenVI' } else { 'CNN_MOYAN' }
    if ($record.playerName -ne $expectedName -or $record.resolution -ne '2560x1440') {
        throw "Client identity/resolution mismatch: $recordPath"
    }
    if (@($record.requiredJvmArgs).Count -ne 0) { throw "Unexpected JVM arguments: $recordPath" }
    if ($record.compatibilityFixture) { throw "Unexpected compatibility fixture: $recordPath" }

    $expectedMods = @()
    if ($record.profile -ne 'S1') {
        $expectedMods += 'CustomSkinLoader:15.0.1-Universal'
        if ($record.node -eq '26.2-fabric') { $expectedMods += 'ModMenu:20.0.1' }
    }
    if ((@($expectedMods | Sort-Object) -join "`n") -ne (@(@($record.requiredTestMods) | Sort-Object) -join "`n")) {
        throw "Profile test mod matrix mismatch: $recordPath"
    }

    if (Test-Path -LiteralPath $gameDirectory -PathType Container) {
        $verifiedInstances++
        $requiredFiles = @($expectedMods | ForEach-Object { $testModFiles[$_] })
        foreach ($knownFile in $knownTestModFiles) {
            $path = Join-Path $modsDirectory $knownFile
            $shouldExist = $knownFile -in $requiredFiles
            if ($shouldExist -ne (Test-Path -LiteralPath $path -PathType Leaf)) {
                throw "Profile known test mod presence mismatch: $path"
            }
            if ($shouldExist -and (Get-Sha512 $path) -ne $artifactHashes[$knownFile]) {
                throw "Profile known test mod hash mismatch: $path"
            }
        }
    }
}

[ordered]@{
    schemaVersion = 1
    profileRecords = 30
    verifiedRecords = @($recordsToVerify).Count
    verifiedExistingInstances = $verifiedInstances
    externalPaths = 0
    status = 'PASS'
} | ConvertTo-Json -Depth 4
