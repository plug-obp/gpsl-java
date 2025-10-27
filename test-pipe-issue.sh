#!/bin/bash
cd /Users/ciprian/Playfield/repositories/obp3/gpsl-java

# Test parsing with single pipe
cat > /tmp/test_pipe_single.gpsl << 'EOF'
pp = true
qq = false
test = pp | qq
EOF

echo "Testing: pp | qq"
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home ./gradlew :gpsl-core:test --tests "gpsl.syntax.ReaderTest.testReadAndLinkDeclarations" -Dtest.source="$(cat /tmp/test_pipe_single.gpsl)" 2>&1 | grep -A 5 "error\|Error\|FAILED\|SUCCESS"

echo ""
echo "Testing: pp || qq"
cat > /tmp/test_pipe_double.gpsl << 'EOF'
pp = true
qq = false
test = pp || qq
EOF

JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home ./gradlew :gpsl-core:test --tests "gpsl.syntax.ReaderTest.testReadAndLinkDeclarations" -Dtest.source="$(cat /tmp/test_pipe_double.gpsl)" 2>&1 | grep -A 5 "error\|Error\|FAILED\|SUCCESS"
