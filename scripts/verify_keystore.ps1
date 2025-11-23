$keystorePropsPath = "keystore.properties"
$keystorePath = "keystore/release_new.keystore"

if (-not (Test-Path $keystorePropsPath)) {
    Write-Host "Error: $keystorePropsPath not found."
    exit 1
}

if (-not (Test-Path $keystorePath)) {
    Write-Host "Error: $keystorePath not found."
    exit 1
}

$props = Get-Content $keystorePropsPath | ConvertFrom-StringData
$storePass = $props.RELEASE_STORE_PASSWORD
$keyAlias = $props.RELEASE_KEY_ALIAS

if ([string]::IsNullOrWhiteSpace($storePass)) {
    Write-Host "Error: RELEASE_STORE_PASSWORD not found in $keystorePropsPath."
    exit 1
}

Write-Host "Verifying keystore with provided password..."
try {
    # Try to list keystore content using keytool (requires Java/JDK in PATH)
    $keytoolOutput = keytool -list -v -keystore $keystorePath -storepass $storePass 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Success! Keystore password is correct."
        Write-Host "Alias '$keyAlias' found: $($keytoolOutput -match $keyAlias)"
    } else {
        Write-Host "Failure! Keystore password incorrect or keystore corrupted."
        Write-Host "Keytool Output:"
        Write-Host $keytoolOutput
    }
} catch {
    Write-Host "Error running keytool: $_"
}
