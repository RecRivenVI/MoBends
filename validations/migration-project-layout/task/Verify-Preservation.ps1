$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$result=Join-Path $repo 'validations/migration-project-layout/result'
$before=Get-Content (Join-Path $result 'runtime-before.json') -Raw|ConvertFrom-Json
$relocationsPath=Join-Path $repo 'validations/regression-client-runtime/task/runtime-relocations.json'
$relocations=if(Test-Path -LiteralPath $relocationsPath){@(Get-Content -LiteralPath $relocationsPath -Raw|ConvertFrom-Json)}else{@()}
$count=0
foreach($entry in $before){
    $relative=$entry.path.Replace('run/instances/','validations/regression-client-runtime/instance/')
    foreach($relocation in $relocations){if($relative.StartsWith($relocation.from+'/')){$relative=$relocation.to+$relative.Substring($relocation.from.Length);break}}
    $path=[IO.Path]::GetFullPath((Join-Path $repo $relative))
    if(-not $path.StartsWith($repo+'\',[StringComparison]::OrdinalIgnoreCase)){throw 'Runtime relocation escaped repository.'}
    $file=Get-Item -LiteralPath $path
    if($file.Length -ne $entry.length -or $file.LastWriteTimeUtc.Ticks -ne $entry.modifiedUtcTicks -or [string]$file.Attributes -ne $entry.attributes -or (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash -ne $entry.sha256){throw "Preserved runtime mismatch: $path"}
    $count++
}
"PASS: $count runtime files retain content, size, modification timestamp and attributes."
