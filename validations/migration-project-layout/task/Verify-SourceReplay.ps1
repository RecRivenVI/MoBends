$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$map=Get-Content (Join-Path $repo 'validations/migration-project-layout/result/source-replay.json') -Raw|ConvertFrom-Json
function Tokens([string]$text) {
    $pattern='(?s)""".*?"""|"(?:\\.|[^"\\])*"|''(?:\\.|[^''\\])*''|/\*.*?\*/|//[^\r\n]*|\s+|[A-Za-z_$][A-Za-z0-9_$]*|\d+(?:\.\d+)?|.'
    $tokens=[Collections.Generic.List[string]]::new()
    $inImport=$false
    foreach($m in [regex]::Matches($text,$pattern)) {
        $v=$m.Value
        if($v -match '^\s+$' -or $v.StartsWith('//') -or $v.StartsWith('/*')){continue}
        if($v -eq 'import'){$inImport=$true;continue}
        if($inImport){if($v-eq';'){$inImport=$false};continue}
        $tokens.Add($v)
    }
    return $tokens -join "`u{001f}"
}
$count=0;$formatted=0
foreach($entry in $map) {
    $source=[string]$entry.source
    if($source.StartsWith('src/')){$source=$source.Replace('src/','validations/migration-project-layout/instance/original-authored-src/')}
    elseif($source -match '^versions/([^/]+)/build/generated/stonecutter/(.*)$'){$source="validations/migration-project-layout/instance/replay/$($matches[1])/$($matches[2])"}
    $sourcePath=Join-Path $repo $source
    $destination=Join-Path $repo $entry.destination
    if((Get-FileHash $sourcePath -Algorithm SHA256).Hash-ne$entry.sha256){throw "Replay input changed: $source"}
    if((Get-FileHash $destination -Algorithm SHA256).Hash-ne$entry.sha256){
        if(-not $destination.EndsWith('.java')){throw "Resource changed: $destination"}
        if((Tokens ([IO.File]::ReadAllText($sourcePath)))-cne(Tokens ([IO.File]::ReadAllText($destination)))){throw "Java executable token mismatch: $destination"}
        $formatted++
    }
    $count++
}
"PASS: $count source/resource files; $formatted Java files differ only in formatting, comments or imports."
