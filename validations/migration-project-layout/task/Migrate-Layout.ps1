$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
Set-Location $repo
$nodes = @('1.20.1-forge','1.21.1-neoforge','26.1.2-neoforge','26.2-fabric','26.2-neoforge')
$validation = 'validations/regression-client-runtime'
$resultRoot = 'validations/migration-project-layout/result'
New-Item -ItemType Directory -Force $resultRoot | Out-Null
function Relative([string]$path) { [IO.Path]::GetRelativePath($repo,$path).Replace('\','/') }
function Inventory([string]$root) {
    @(Get-ChildItem -LiteralPath $root -File -Recurse -Force | ForEach-Object {
        [ordered]@{ path=Relative $_.FullName; length=$_.Length; sha256=(Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash; modifiedUtcTicks=$_.LastWriteTimeUtc.Ticks; attributes=[string]$_.Attributes }
    })
}
function WriteText([string]$path,[string]$text) {
    $absolute = [IO.Path]::GetFullPath((Join-Path $repo $path))
    if (-not $absolute.StartsWith($repo + '\')) { throw "Path outside repository: $path" }
    New-Item -ItemType Directory -Force (Split-Path $absolute) | Out-Null
    [IO.File]::WriteAllText($absolute,$text.Replace("`r`n","`n"),[Text.UTF8Encoding]::new($false))
}
function MoveSafe([string]$from,[string]$to) {
    $source = [IO.Path]::GetFullPath((Join-Path $repo $from))
    $destination = [IO.Path]::GetFullPath((Join-Path $repo $to))
    if (-not $source.StartsWith($repo+'\') -or -not $destination.StartsWith($repo+'\')) { throw 'Move escaped repository' }
    if (Test-Path -LiteralPath $destination) { throw "Destination exists: $to" }
    New-Item -ItemType Directory -Force (Split-Path $destination) | Out-Null
    Move-Item -LiteralPath $source -Destination $destination
}
$runtimeBefore = Inventory 'run/instances'
WriteText "$resultRoot/runtime-before.json" (($runtimeBefore | ConvertTo-Json -Depth 6)+"`n")
$sourceMap = @()
foreach($node in $nodes) {
    $source = if($node -eq '26.2-fabric'){'src'}else{"versions/$node/build/generated/stonecutter"}
    $destination = "versions/$node/src"
    if(Test-Path $destination){throw "Source destination exists: $destination"}
    if($node -eq '26.2-fabric') { Copy-Item -LiteralPath $source -Destination $destination -Recurse }
    else {
        New-Item -ItemType Directory -Force $destination | Out-Null
        foreach($set in @('main','test')) { Copy-Item -LiteralPath "$source/$set" -Destination "$destination/$set" -Recurse }
    }
    foreach($f in Get-ChildItem $destination -File -Recurse) {
        $rel = [IO.Path]::GetRelativePath((Join-Path $repo $destination),$f.FullName).Replace('\','/')
        $origin = "$source/$rel"
        $hash = (Get-FileHash $f.FullName -Algorithm SHA256).Hash
        if($hash -ne (Get-FileHash $origin -Algorithm SHA256).Hash){throw "Source replay mismatch: $origin"}
        $sourceMap += [ordered]@{ target=$node; source=$origin; destination=Relative $f.FullName; sha256=$hash }
    }
}
WriteText "$resultRoot/source-replay.json" (($sourceMap|ConvertTo-Json -Depth 5)+"`n")
# Full original authored source is in the signed Update baseline. Every target
# above has a byte-checked effective Stonecutter replay before the source move.
MoveSafe 'src' 'validations/migration-project-layout/instance/original-authored-src'
MoveSafe 'run/instances' "$validation/instance"
MoveSafe 'run/profiles' "$validation/task/profiles"
MoveSafe 'run/cache' "$validation/instance/artifact-cache"
foreach($f in Get-ChildItem "$validation/task/profiles" -Filter *.json -File -Recurse) {
    $text=[IO.File]::ReadAllText($f.FullName).Replace('run/instances/',"$validation/instance/")
    WriteText (Relative $f.FullName) $text
}
# Runtime tooling remains a development tool; one implementation serves all profiles.
foreach($name in @('Stage-Profile.ps1','Verify-Profiles.ps1')) {
    $text=[IO.File]::ReadAllText((Join-Path $repo "eng/runtime/$name"))
    $text=$text.Replace('run\profiles',"$validation/task/profiles").Replace('run/instances',"$validation/instance").Replace('run\instances',"$validation/instance").Replace('run\cache\mods',"$validation/instance/artifact-cache/mods")
    WriteText "eng/runtime/$name" $text
}
$common=@{
 'mod.id'='mobends';'mod.name'="Mo' Bends";'mod.group'='io.github.recrivenvi';'mod.package'='io.github.recrivenvi.mobends';'mod.version'='5.1.7';'mod.license'='MIT';'mod.authors'='RecRivenVI';'mod.description'='Mo\u0027 Bends adds expressive and fluid animations to Minecraft players and creatures.';'deps.junit'='6.0.3'
}
$facts=@{
 '1.20.1-forge'=@{'target.minecraft'='1.20.1';'target.java'='17';'deps.forge_loader'='1.20.1-47.4.23';'target.loader.compatibility'='[47,)';'target.minecraft.compatibility'='[1.20.1,1.21)'}
 '1.21.1-neoforge'=@{'target.minecraft'='1.21.1';'target.java'='21';'deps.neo_loader'='21.1.248';'target.loader.compatibility'='[4,)';'target.minecraft.compatibility'='[1.21.1,1.22)'}
 '26.1.2-neoforge'=@{'target.minecraft'='26.1.2';'target.java'='25';'deps.neo_loader'='26.1.2.97';'target.loader.compatibility'='[26.1.2,)';'target.minecraft.compatibility'='[26.1.2,26.2)'}
 '26.2-fabric'=@{'target.minecraft'='26.2';'target.java'='25';'deps.fabric_loader'='0.19.3';'deps.fabric_api'='0.156.0+26.2';'deps.modmenu'='20.0.1';'target.loader.compatibility'='>=0.19.3';'target.minecraft.compatibility'='~26.2'}
 '26.2-neoforge'=@{'target.minecraft'='26.2';'target.java'='25';'deps.neo_loader'='26.2.0.66';'target.loader.compatibility'='[26.2,)';'target.minecraft.compatibility'='[26.2,26.3)'}
}
foreach($node in $nodes) {
    $loader=($node -split '-',2)[1]
    $text=[IO.File]::ReadAllText((Join-Path $repo "build.$loader.gradle.kts"))
    $text=$text.Replace('sc.current.version','project.property("target.minecraft")').Replace('sc.current.project','project.name')
    $text=$text.Replace('sc.current.parsed >= "26.1"','project.property("target.java") == "25"').Replace('sc.current.parsed <= "1.21.1"','project.name == "1.21.1-neoforge"').Replace('sc.current.parsed >= "26.2"','project.name == "26.2-neoforge"')
    $text=$text.Replace('run/profiles/',"$validation/task/profiles/").Replace('run/instances',"$validation/instance")
    $text=$text.Replace('JsonSlurper().parse(record.asFile)','JsonSlurper().parseText(providers.fileContents(record).asText.get())')
    $text=[regex]::Replace($text,'tasks.named\("createMinecraftArtifacts"\)\s*\{\s*dependsOn\("stonecutterGenerate"\)\s*\}\s*','')
    if($loader -eq 'fabric') {
        $text=$text.Replace('id("dev.kikugie.loom-back-compat")','id("net.fabricmc.fabric-loom") version "1.17.19"')
        $text=$text.Replace('    loomx.applyMojangMappings()', '')
        $text=$text.Replace('loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile }','tasks.named<Jar>("jar").flatMap { it.archiveFile }, tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile }')
    }
    WriteText "versions/$node/build.gradle.kts" $text
    $p=$facts[$node];$p['target.status']='implemented';$p['target.client']='true';$p['target.client_multiplayer']='true'
    WriteText "versions/$node/target.properties" ((@($p.Keys|Sort-Object|ForEach-Object{"$_=$($p[$_])"})-join"`n")+"`n")
}
$oldProps=[IO.File]::ReadAllText((Join-Path $repo 'gradle.properties'))
$oldProps=[regex]::Replace($oldProps,'(?m)^loomx.loom_version=.*\r?\n?','')
WriteText 'gradle.properties' ($oldProps.TrimEnd()+"`n"+((@($common.Keys|Sort-Object|ForEach-Object{"$_=$($common[$_])"})-join"`n"))+"`n")
foreach($f in @('build.fabric.gradle.kts','build.forge.gradle.kts','build.neoforge.gradle.kts','stonecutter.gradle.kts','stonecutter.properties.toml','versions/current')) {
    MoveSafe $f "validations/migration-project-layout/instance/baseline-build/$f"
}
$runtimeAfter = @()
foreach($e in $runtimeBefore) {
    $to=$e.path.Replace('run/instances/',"$validation/instance/")
    $f=Get-Item -LiteralPath $to
    if($e.sha256 -ne (Get-FileHash -LiteralPath $to -Algorithm SHA256).Hash -or $e.length -ne $f.Length -or $e.modifiedUtcTicks -ne $f.LastWriteTimeUtc.Ticks -or $e.attributes -ne [string]$f.Attributes){throw "Runtime mismatch: $to"}
    $runtimeAfter += [ordered]@{source=$e.path;destination=$to;sha256=$e.sha256}
}
WriteText "$resultRoot/runtime-map.json" (($runtimeAfter|ConvertTo-Json -Depth 5)+"`n")
'Source and runtime migration verified.'
