$ErrorActionPreference = "Stop"
$backendDir = "E:\иу5\teaching-system\backend"
$jdkHome = "E:\иу5\jdk17\jdk-17.0.19+10"

Write-Host "=== Step 1: Build JAR with Docker Maven ==="
docker run --rm -v "${backendDir}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean package -DskipTests -q
if ($LASTEXITCODE -ne 0) { Write-Host "Build failed!"; exit 1 }

Write-Host "=== Step 2: Start backend ==="
$env:JAVA_HOME = $jdkHome
$jar = Get-ChildItem "$backendDir\target\*.jar" | Select-Object -First 1
Write-Host "JAR: $($jar.FullName)"
Write-Host "JDK: $jdkHome"

& "$jdkHome\bin\java.exe" -jar $jar.FullName
