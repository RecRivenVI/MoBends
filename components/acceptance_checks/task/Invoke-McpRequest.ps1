[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$Instance,
    [Parameter(Mandatory)][string]$Route,
    [ValidateSet('GET','POST')][string]$Method = 'GET',
    [string]$Body,
    [Parameter(Mandatory)][string]$Evidence,
    [switch]$Image
)
$ErrorActionPreference = 'Stop'
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..'))
$game = [IO.Path]::GetFullPath((Join-Path $root $Instance)).TrimEnd('\','/')
if (-not $game.StartsWith($root + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) { throw 'Instance escapes repository' }
if ($Instance -notmatch '^(validations/smoke-mcp_(bootstrap|animation)/instance|instances)/26\.3-neoforge/client$') { throw 'Not an owned MCP client' }
if ($Route -notmatch '^/v0/[A-Za-z0-9_/?=.,:&%-]+$' -or $Evidence -notmatch '^[a-z0-9_-]+$') { throw 'Invalid route or evidence name' }
$record = Get-Content -LiteralPath (Join-Path $game 'minecraft-protocol/endpoint.json') -Raw | ConvertFrom-Json
if ($record.schemaVersion -ne 1 -or $record.state -ne 'RUNNING' -or $record.target -ne '26.3-neoforge' -or $record.tokenFile -ne 'token' -or $record.baseUrl -ne "http://127.0.0.1:$($record.port)" -or [IO.Path]::GetFullPath($record.gameDirectory).TrimEnd('\','/') -ne $game) { throw 'Endpoint identity mismatch' }
$process = Get-Process -Id $record.pid -ErrorAction Stop
if ([Math]::Abs(($process.StartTime.ToUniversalTime() - ([DateTimeOffset]$record.processStartedAt).UtcDateTime).TotalSeconds) -gt 1) { throw 'Process identity mismatch' }
$headers = @{
    Authorization = 'Bearer ' + (Get-Content -LiteralPath (Join-Path $game 'minecraft-protocol/token') -Raw).Trim()
    'X-MCP-Runtime-Instance' = $record.runtimeInstanceId
    'X-MCP-Deadline-Ms' = '30000'
}
$session = Invoke-RestMethod "$($record.baseUrl)/v0/session" -Headers $headers -TimeoutSec 5 -MaximumRedirection 0
if ($session.runtimeConnection.runtimeInstanceId -ne $record.runtimeInstanceId -or $session.target -ne '26.3-neoforge') { throw 'Online identity mismatch' }
$bindingFile = Join-Path $game '.mobends-mcp-binding.json'
$binding = if (Test-Path -LiteralPath $bindingFile) { Get-Content -LiteralPath $bindingFile -Raw | ConvertFrom-Json } else { [pscustomobject]@{ runtimeInstanceId = $record.runtimeInstanceId; leaseId = $null } }
if ($binding.runtimeInstanceId -ne $record.runtimeInstanceId) { throw 'Runtime restarted: refuse implicit rebinding' }
if ($binding.leaseId) { $headers['X-MCP-Control-Lease'] = $binding.leaseId }
$resultDirectory = if ($Instance.StartsWith('validations/')) { Join-Path $root (($Instance -split '/instance/')[0] + '/result') } else { Join-Path $root 'build/user-acceptance' }
[IO.Directory]::CreateDirectory($resultDirectory) | Out-Null
if ($Image) {
    $destination = Join-Path $resultDirectory ($Evidence + '.png')
    if (Test-Path -LiteralPath $destination) { throw 'Do not overwrite captured evidence' }
    Invoke-WebRequest "$($record.baseUrl)$Route" -Headers $headers -TimeoutSec 30 -MaximumRedirection 0 -OutFile $destination
    $probe = Join-Path $resultDirectory 'mcp-state.json'
    if (Test-Path -LiteralPath $probe) { Copy-Item -LiteralPath $probe -Destination (Join-Path $resultDirectory ($Evidence + '-probe.json')) }
    Write-Output $destination
} else {
    $request = @{ Uri = "$($record.baseUrl)$Route"; Method = $Method; Headers = $headers; TimeoutSec = 35; MaximumRedirection = 0 }
    if ($Method -eq 'POST') { $request.Body = if ($Body) { $Body } else { '{}' }; $request.ContentType = 'application/json' }
    try { $result = Invoke-RestMethod @request } catch {
        $failure = [ordered]@{ route = $Route; runtimeInstanceId = $record.runtimeInstanceId; error = $_.ErrorDetails.Message; retryAttempted = $false }
        [IO.File]::WriteAllText((Join-Path $resultDirectory ($Evidence + '-error.json')), ($failure | ConvertTo-Json -Depth 10) + "`n", [Text.UTF8Encoding]::new($false))
        throw
    }
    if ($Route -eq '/v0/control/acquire' -and $result.leaseId) { $binding.leaseId = $result.leaseId }
    if ($Route -eq '/v0/control/release') { $binding.leaseId = $null }
    # Lease credentials remain inside the ignored instance, never in evidence or tool output.
    if ($result.PSObject.Properties['leaseId']) { $result.PSObject.Properties.Remove('leaseId') }
    if ($result.PSObject.Properties['previousLeaseId']) { $result.PSObject.Properties.Remove('previousLeaseId') }
    $serialized = $result | ConvertTo-Json -Depth 70
    [IO.File]::WriteAllText((Join-Path $resultDirectory ($Evidence + '.json')), $serialized + "`n", [Text.UTF8Encoding]::new($false))
    Write-Output $serialized
}
[IO.File]::WriteAllText($bindingFile, ($binding | ConvertTo-Json) + "`n", [Text.UTF8Encoding]::new($false))
