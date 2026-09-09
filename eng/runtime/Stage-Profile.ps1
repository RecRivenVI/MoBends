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

function Get-PinnedArtifact([hashtable] $Artifact, [string] $CacheDirectory) {
    New-Item -ItemType Directory -Force -Path $CacheDirectory | Out-Null
    $cachePath = Join-Path $CacheDirectory $Artifact.File
    if (Test-Path -LiteralPath $cachePath -PathType Leaf) {
        if ((Get-Sha512 $cachePath) -eq $Artifact.Sha512) { return $cachePath }
        Remove-Item -LiteralPath $cachePath -Force
    }

    $partial = "$cachePath.partial-$([Guid]::NewGuid().ToString('N'))"
    try {
        Invoke-WebRequest -Uri $Artifact.Uri -OutFile $partial -UseBasicParsing
        if ((Get-Sha512 $partial) -ne $Artifact.Sha512) {
            throw "Downloaded pinned artifact hash mismatch: $($Artifact.Uri)"
        }
        Move-Item -LiteralPath $partial -Destination $cachePath
    }
    finally {
        if (Test-Path -LiteralPath $partial) { Remove-Item -LiteralPath $partial -Force }
    }
    return $cachePath
}

$artifacts = @{
    'CustomSkinLoader:15.0.1-Universal' = @{
        File = 'CustomSkinLoader_Universal-15.0.1.jar'
        Uri = 'https://github.com/xfl03/MCCustomSkinLoader/releases/download/v15.0.1/CustomSkinLoader_Universal-15.0.1.jar'
        Sha512 = '8c65193c46c1435ddea571901f1dd04cc179179f59f1c1a0449349de04722b9c1b6e5efaec166975e05d2fa0b05f6c07c9d5d93a05f1c9a57fece2a0d0fc1ce7'
    }
    'ModMenu:20.0.1' = @{
        File = 'modmenu-20.0.1.jar'
        Uri = 'https://maven.terraformersmc.com/releases/com/terraformersmc/modmenu/20.0.1/modmenu-20.0.1.jar'
        Sha512 = '1aa297ab5e6fac71ad6af750fe6f8cd25281bd00c0340e5fe1c1e9d153d1198df03be1df55727a4a2116db97dcaad541d41754f3fc0063abfe4345b60f193fb4'
    }
}
$knownTestModFiles = @('CustomSkinLoader_Universal-15.0.1.jar', 'modmenu-20.0.1.jar', 'zume-1.2.2.jar')
$recordPaths = if ($ProfileRecord) {
    @((Resolve-Path -LiteralPath $ProfileRecord).Path)
}
else {
    @(Get-ChildItem -LiteralPath $profilesRoot -File -Recurse -Filter '*.json' | Select-Object -ExpandProperty FullName)
}

$cacheDirectory = Join-Path $repository 'run\cache\mods'
$results = foreach ($recordPath in $recordPaths) {
    if (-not $recordPath.StartsWith($profilesRoot + '\', [StringComparison]::OrdinalIgnoreCase)) {
        throw "Profile record is outside the repository profile root: $recordPath"
    }

    $record = Get-Content -LiteralPath $recordPath -Raw | ConvertFrom-Json
    $gameDirectory = Resolve-RepositoryRelativePath ([string] $record.gameDirectory)
    $modsDirectory = Resolve-RepositoryRelativePath ([string] $record.modsDirectory)
    $instancesRoot = [IO.Path]::GetFullPath((Join-Path $repository 'run\instances'))
    if (-not $gameDirectory.StartsWith($instancesRoot + '\', [StringComparison]::OrdinalIgnoreCase)) {
        throw "Profile gameDirectory is outside run/instances: $recordPath"
    }
    if ($modsDirectory -ne (Join-Path $gameDirectory 'mods')) {
        throw "Profile modsDirectory must equal gameDirectory/mods: $recordPath"
    }
    if ($record.compatibilityFixture) { throw "No compatibility fixture is authorized: $recordPath" }

    $requiredTestMods = @($record.requiredTestMods)
    foreach ($required in $requiredTestMods) {
        if (-not $artifacts.ContainsKey([string] $required)) { throw "Unknown required test mod: $required" }
    }

    New-Item -ItemType Directory -Force -Path $modsDirectory | Out-Null
    $requiredFiles = @($requiredTestMods | ForEach-Object { $artifacts[[string] $_].File })
    foreach ($knownFile in $knownTestModFiles) {
        $destination = Join-Path $modsDirectory $knownFile
        if ($knownFile -notin $requiredFiles -and (Test-Path -LiteralPath $destination -PathType Leaf)) {
            Remove-Item -LiteralPath $destination -Force
        }
    }

    foreach ($required in $requiredTestMods) {
        $artifact = $artifacts[[string] $required]
        $cached = Get-PinnedArtifact $artifact $cacheDirectory
        $destination = Join-Path $modsDirectory $artifact.File
        if (-not (Test-Path -LiteralPath $destination -PathType Leaf) -or (Get-Sha512 $destination) -ne $artifact.Sha512) {
            Copy-Item -LiteralPath $cached -Destination $destination -Force
        }
    }

    [ordered]@{
        profileRecord = [IO.Path]::GetRelativePath($repository, $recordPath).Replace('\', '/')
        gameDirectory = [IO.Path]::GetRelativePath($repository, $gameDirectory).Replace('\', '/')
        requiredTestMods = $requiredTestMods
        staged = $true
    }
}

[ordered]@{
    schemaVersion = 1
    stagedProfiles = @($results).Count
    results = @($results)
    externalDependencies = 0
    status = 'PASS'
} | ConvertTo-Json -Depth 6
