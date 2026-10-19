param([switch]$SelfTest)

$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$nodes = @('1.20.1-forge','1.21.1-neoforge','26.1.2-neoforge','26.2-fabric','26.2-neoforge')
$categories = @('usage','development','design','project','reference','release')
$standardDocs = @('README.md','AGENTS.md','CLAUDE.md','CONTRIBUTING.md','SECURITY.md','CODE_OF_CONDUCT.md','LICENSE.md','LICENSE','NOTICE','NOTICE.md','COPYING','AUTHORS')

function Test-DocumentName([string]$category,[string]$name) {
    if($name.StartsWith('adr-')) { return $category -eq 'design' -and $name -cmatch '^adr-[0-9]+(?:\.[0-9]+)*-[a-z0-9]+(?:-[a-z0-9]+)*\.md$' }
    if($name.StartsWith('phase-')) { return $category -eq 'project' -and $name -cmatch '^phase-[0-9]+(?:\.[0-9]+)*-[a-z0-9]+(?:-[a-z0-9]+)*\.md$' }
    return $name -cmatch '^[a-z]+-[a-z0-9]+(?:-[a-z0-9]+)*\.md$'
}

# Classify only governed boundaries. Native source, generated output, runtime
# contents and third-party snapshots are not project-document filename spaces.
function Get-PathIssue([string]$path,[bool]$directory) {
    $parts=$path.Replace('\','/').Split('/')
    if($parts.Count-eq1) {
        if(-not $directory -and $path.EndsWith('.md',[StringComparison]::OrdinalIgnoreCase) -and $path -notin $standardDocs) { return 'Ordinary root document belongs in documents/<category>.' }
        if($directory -and $path -in @('src','docs','runs','tests','results','.local')) { return 'Old root layout requires review and relocation; it is not an allowed permanent exception.' }
        return
    }
    if($parts[0]-eq'versions') {
        if($parts[1] -notin $nodes) { return 'Unknown or hidden Target entry.' }
        return
    }
    if($parts[0]-eq'documents') {
        if($parts[1] -notin $categories) { return 'Unknown or hidden document category.' }
        if($parts.Count-eq2 -and -not $directory) { return 'Document must be inside a category.' }
        if($parts.Count-gt3 -or ($parts.Count-eq3 -and $directory)) { return 'Nested document category directory is forbidden.' }
        if($parts.Count-eq3 -and $parts[2].EndsWith('.md',[StringComparison]::OrdinalIgnoreCase) -and -not (Test-DocumentName $parts[1] $parts[2])) { return 'Invalid document filename.' }
        return
    }
    if($parts[0]-eq'validations') {
        if($parts[1] -cnotmatch '^[a-z]+-[a-z0-9]+(?:-[a-z0-9]+)*$') { return 'Invalid or hidden validation project name.' }
        if($parts.Count-eq2 -and -not $directory) { return 'Validation files require a named validation project.' }
        if($parts.Count-ge3) {
            if($parts[2] -in @('task','instance','result')) {
                if($parts.Count-eq3 -and -not $directory) { return 'Validation task/instance/result must be directories.' }
                if($parts.Count-eq4 -and $parts[2]-eq'task' -and $parts[3] -in @('validation.md','validation-guide.md')) { return 'Validation instructions belong at validation project root.' }
                return
            }
            if($parts.Count-eq3 -and -not $directory -and $parts[2] -in @('validation.md','validation-guide.md')) { return }
            return 'Unexpected validation project root entry (including hidden directories).'
        }
        return
    }
    if($parts[0]-eq'instances') {
        if($parts[1] -notin $nodes) { return 'Unknown daily instance Target.' }
        if($parts.Count-ge3 -and $parts[2] -notin @('client','client-multiplayer','server')) { return 'Daily instance must use a genuine variant; profiles are not variants.' }
        return
    }
    if($parts[0]-eq'components' -and $parts[1] -cnotmatch '^[a-z0-9]+(?:-[a-z0-9]+)*$') { return 'Invalid component name.' }
}

if($SelfTest) {
    $cases=@(
        @('documents/.hidden/x.md',$false,$true),
        @('documents/development/.hidden',$true,$true),
        @('documents/development/guide/a.md',$false,$true),
        @('documents/design/adr-2.10-rendering.md',$false,$false),
        @('documents/project/phase-0-baseline.md',$false,$false),
        @('documents/design/phase-1-baseline.md',$false,$true),
        @('documents/reference/inventory-armor.md',$false,$false),
        @('documents/usage/guide-player.md',$false,$false),
        @('documents/development/guide-player.md',$false,$false),
        @('documents/reference/diagram.svg',$false,$false),
        @('README.md',$false,$false),
        @('SECURITY.md',$false,$false),
        @('MIGRATION.md',$false,$true),
        @('validations/.local',$true,$true),
        @('validations/regression-client-runtime/.local',$true,$true),
        @('validations/regression-client-runtime/results',$true,$true),
        @('validations/regression-client-runtime/task/validation.md',$false,$true),
        @('validations/regression-client-runtime/task/fixtures/a.json',$false,$false),
        @('validations/regression-client-runtime/instance/.fabric',$true,$false),
        @('versions/.hidden',$true,$true),
        @('versions/26.2-fabric/src/test/java/Test.java',$false,$false),
        @('instances/26.2-fabric/s2',$true,$true),
        @('instances/26.2-fabric/client/.fabric',$true,$false),
        @('components/player-ui/src/main/java/Test.java',$false,$false),
        @('eng',$true,$false),
        @('.local',$true,$true)
    )
    foreach($case in $cases) {
        $actual=[bool](Get-PathIssue $case[0] $case[1])
        if($actual-ne$case[2]) { throw "Layout regression failed: $($case[0])" }
    }
    "PASS: $($cases.Count) in-memory layout cases; no fixture files written."
    return
}

$issues=[Collections.Generic.List[string]]::new()
function Inspect([IO.FileSystemInfo]$entry) {
    $relative=[IO.Path]::GetRelativePath($repo,$entry.FullName).Replace('\','/')
    $issue=Get-PathIssue $relative $entry.PSIsContainer
    if($issue) { $issues.Add("${relative}: $issue") }
}
foreach($entry in Get-ChildItem -LiteralPath $repo -Force) { Inspect $entry }
$knownRootDirectories=@('.git','.github','.gradle','.kotlin','.idea','.vscode','.fleet','build','out','versions','components','instances','documents','validations','gradle','buildSrc','eng','run')
foreach($entry in Get-ChildItem -LiteralPath $repo -Directory -Force) {
    if($entry.Name -notin $knownRootDirectories) { Write-Warning "Unclassified root directory requires ownership review: $($entry.Name). The template does not automatically authorize or forbid it." }
}
foreach($rootName in @('versions','components','instances')) {
    $root=Join-Path $repo $rootName
    if(Test-Path -LiteralPath $root) {
        foreach($entry in Get-ChildItem -LiteralPath $root -Force) {
            Inspect $entry
            if($rootName-eq'instances' -and $entry.PSIsContainer) { foreach($variant in Get-ChildItem -LiteralPath $entry.FullName -Force) { Inspect $variant } }
        }
    }
}
$documents=Join-Path $repo 'documents'
if(Test-Path -LiteralPath $documents) { foreach($entry in Get-ChildItem -LiteralPath $documents -Force -Recurse) { Inspect $entry } }
$validations=Join-Path $repo 'validations'
if(Test-Path -LiteralPath $validations) {
    foreach($project in Get-ChildItem -LiteralPath $validations -Force) {
        Inspect $project
        if($project.PSIsContainer) {
            foreach($entry in Get-ChildItem -LiteralPath $project.FullName -Force) { Inspect $entry }
            if(-not(Test-Path -LiteralPath (Join-Path $project.FullName 'validation.md') -PathType Leaf)) { $issues.Add("$($project.Name): missing validation.md") }
            if(-not(Test-Path -LiteralPath (Join-Path $project.FullName 'task') -PathType Container)) { $issues.Add("$($project.Name): missing task directory") }
            foreach($name in @('validation.md','validation-guide.md')) { $p=Join-Path $project.FullName "task/$name"; if(Test-Path -LiteralPath $p){ Inspect (Get-Item -LiteralPath $p) } }
        }
    }
}
if($issues.Count) { throw ($issues -join "`n") }
foreach ($node in $nodes) {
    foreach ($entry in @('build.gradle.kts','target.properties','src/main/java','src/main/resources','src/test/java')) {
        if (-not (Test-Path -LiteralPath (Join-Path $repo "versions/$node/$entry"))) { throw "Missing $node/$entry" }
    }
    $sources = @(Get-ChildItem -LiteralPath (Join-Path $repo "versions/$node/src/main/java") -File -Recurse -Filter '*.java')
    if (-not $sources.Count) { throw "Empty target $node" }
}
if (Test-Path -LiteralPath (Join-Path $repo '.github/workflows')) { throw 'Unexpected workflow.' }
$run=Join-Path $repo 'run'
if(Test-Path -LiteralPath $run) {
    if(@(Get-ChildItem -LiteralPath $run -Force).Count) { throw 'Old run directory contains data: classify it before claiming layout compliance.' }
    Write-Warning 'Empty old run/ shell still exists; retained after blocked by policy. This is reported residue, not a permanent layout exception.'
}
"PASS: governed Target, document, validation and instance boundaries; native interiors not classified as project documents."
