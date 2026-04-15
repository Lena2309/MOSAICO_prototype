# export JAVA_HOME=...

PASSED=0
FAILED=0
CPT=0

cd ..

FILES=("test/test_cases/test0.sysml" "test/test_cases/test0_2.sysml" "test/test_cases/test0_3.sysml" "test/test_cases/test1.sysml" "test/test_cases/test2.sysml" "test/test_cases/test3.sysml" "test/test_cases/test3.2.sysml" "test/test_cases/test3.3.sysml" "test/test_cases/test4.sysml" "test/test_cases/test5.sysml" "test/test_cases/test5_2.sysml" "test/test_cases/test6.sysml" "test/test_cases/test7.sysml")

for f in "${FILES[@]}"
do
  echo TESTING $f $CPT
  mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args=$f > out.$CPT
  if  grep OUTPUT out.$CPT | grep "Test OK" ; then
    PASSED=$(( PASSED + 1 ))
  fi
  if  grep OUTPUT out.$CPT | grep "Test KO" ; then
    FAILED=$(( FAILED + 1 ))
  fi
   CPT=$(( CPT + 1 ))
done



echo PASSED= $PASSED
echo FAILED = $FAILED
echo CPT = $CPT
