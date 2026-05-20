# export JAVA_HOME=...

PASSED=0
FAILED=0
CPT=0

cd ..

FILES=("test/test_cases/test0.sysml" "test/test_cases/test1.sysml" "test/test_cases/test2.sysml"  "test/test_cases/test3.2.sysml" "test/test_cases/test4.sysml" "test/test_cases/test5.sysml" "test/test_cases/test5_2.sysml" "test/test_cases/test6.sysml" "test/test_cases/test7.sysml" "test/test_cases/test8.sysml" "test/test_cases/test9.sysml")
NON_TERMINATION_FILES=( "test/test_cases/test0_2.sysml" "test/test_cases/test0_3.sysml" "test/test_cases/test3.3.sysml" "test/test_cases/test3.sysml")

# Tests that produce OK or KO
for f in "${FILES[@]}"
do
  echo $CPT: TESTING $f
  mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args=$f &> out.$CPT
  if  grep OUTPUT out.$CPT | grep "Test KO" ; then
      echo FAILED
      FAILED=$(( FAILED + 1 ))
  elif  grep OUTPUT out.$CPT | grep "Test OK" ; then
    echo PASSED
    PASSED=$(( PASSED + 1 ))
  fi
   CPT=$(( CPT + 1 ))
done

# Tests that produce KO or nothing
for f in "${NON_TERMINATION_FILES[@]}"
do
  echo $CPT: TESTING $f
  mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args=$f &> out.$CPT
  if grep OUTPUT out.$CPT | grep "Test KO" ; then
    echo FAILED
    FAILED=$(( FAILED + 1 ))
  else
    echo PASSED
    PASSED=$(( PASSED + 1 ))
  fi
   CPT=$(( CPT + 1 ))
done



echo PASSED= $PASSED
echo FAILED = $FAILED
echo CPT = $CPT
