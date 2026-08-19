$env:JAVA_HOME = 'C:\Users\hp\.jdks\jdk-17'
$javaExe = 'C:\Users\hp\.jdks\jdk-17\bin\java.exe'

# Get all required jar files from maven repo
$m2Repo = 'C:\Users\hp\.m2\repository'
$jars = Get-ChildItem $m2Repo -Recurse -Filter '*.jar' | ForEach-Object { $_.FullName }
$classpath = "target\classes;" + ($jars -join ';')

Write-Host "Executing SamplePdfGenerator via Java 17..."
& $javaExe -cp $classpath com.moroccoit.attestation.util.SamplePdfGenerator
