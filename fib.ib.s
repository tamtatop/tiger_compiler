.data
.text
.globl main
main:
_fun_main:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -556
sw $s0, -548($fp)
sw $s1, -544($fp)
sw $s2, -540($fp)
sw $s3, -536($fp)
sw $s4, -532($fp)
sw $s5, -528($fp)
sw $s6, -524($fp)
sw $s7, -520($fp)
s.s $f20, -516($fp)
s.s $f21, -512($fp)
s.s $f22, -508($fp)
s.s $f23, -504($fp)
sw $ra, -556($fp)
lw $s7, -140($fp)
l.s $f23, -268($fp)
lw $s6, -148($fp)
lw $s5, -272($fp)
l.s $f22, -156($fp)
lw $s4, -164($fp)
l.s $f21, -316($fp)
l.s $f20, -320($fp)
lw $s3, -180($fp)
lw $s2, -304($fp)
lw $s1, -204($fp)
lw $s0, -220($fp)
li $t2, 1
move $t3, $t2
move $s6, $t3
move $t2, $s6
move $t3, $t2
move $s3, $t3
li $t2, 1
move $t3, $t2
move $s7, $t3
move $t2, $s7
move $t3, $t2
move $s1, $t3
li $t2, 5
move $t3, $t2
move $s4, $t3
move $t2, $s4
move $t3, $t2
move $s2, $t3
li.s $f6, 320.000000
mov.s $f7, $f6
mov.s $f22, $f7
mov.s $f6, $f22
mov.s $f7, $f6
s.s $f7, -480($fp)
li.s $f6, 9.810000
mov.s $f7, $f6
s.s $f7, -364($fp)
l.s $f6, -364($fp)
mov.s $f7, $f6
s.s $f7, -300($fp)
li.s $f6, 0.009000
mov.s $f7, $f6
s.s $f7, -360($fp)
l.s $f6, -360($fp)
mov.s $f7, $f6
s.s $f7, -404($fp)
li.s $f6, 0.001000
mov.s $f7, $f6
s.s $f7, -372($fp)
l.s $f6, -372($fp)
mov.s $f7, $f6
s.s $f7, -396($fp)
li.s $f6, 1.225000
mov.s $f7, $f6
s.s $f7, -368($fp)
l.s $f6, -368($fp)
mov.s $f7, $f6
s.s $f7, -216($fp)
li.s $f6, 0.200000
mov.s $f7, $f6
s.s $f7, -380($fp)
l.s $f6, -380($fp)
mov.s $f7, $f6
s.s $f7, -308($fp)
li.s $f6, 0.280000
mov.s $f7, $f6
s.s $f7, -376($fp)
l.s $f6, -376($fp)
mov.s $f7, $f6
s.s $f7, -108($fp)
li.s $f6, 0.050000
mov.s $f7, $f6
s.s $f7, -332($fp)
l.s $f6, -332($fp)
mov.s $f7, $f6
s.s $f7, -476($fp)
li.s $f6, 0.160000
mov.s $f7, $f6
s.s $f7, -324($fp)
l.s $f6, -324($fp)
mov.s $f7, $f6
s.s $f7, -504($fp)
li.s $f6, 1000.000000
mov.s $f7, $f6
s.s $f7, -328($fp)
l.s $f6, -328($fp)
mov.s $f7, $f6
s.s $f7, -444($fp)
li.s $f6, 10.000000
mov.s $f7, $f6
mov.s $f21, $f7
mov.s $f6, $f21
mov.s $f7, $f6
s.s $f7, -184($fp)
li.s $f6, 1025.000000
mov.s $f7, $f6
mov.s $f20, $f7
mov.s $f6, $f20
mov.s $f7, $f6
s.s $f7, -432($fp)
li.s $f6, 2.100000
mov.s $f7, $f6
s.s $f7, -348($fp)
l.s $f6, -348($fp)
mov.s $f7, $f6
s.s $f7, -412($fp)
li.s $f6, 11.600000
mov.s $f7, $f6
s.s $f7, -352($fp)
l.s $f6, -352($fp)
mov.s $f7, $f6
s.s $f7, -196($fp)
li.s $f6, 900.000000
mov.s $f7, $f6
s.s $f7, -340($fp)
l.s $f6, -340($fp)
mov.s $f7, $f6
s.s $f7, -52($fp)
li.s $f6, 140.250000
mov.s $f7, $f6
s.s $f7, -344($fp)
l.s $f6, -344($fp)
mov.s $f7, $f6
s.s $f7, -500($fp)
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -336($fp)
l.s $f6, -336($fp)
mov.s $f7, $f6
s.s $f7, -440($fp)
li.s $f6, 0.000000
mov.s $f7, $f6
mov.s $f23, $f7
mov.s $f6, $f23
mov.s $f7, $f6
s.s $f7, -120($fp)
li $t2, 0
move $t3, $t2
move $s5, $t3
move $t2, $s5
move $t3, $t2
move $s0, $t3
sw $s7, -140($fp)
s.s $f23, -268($fp)
sw $s6, -148($fp)
sw $s5, -272($fp)
s.s $f22, -156($fp)
sw $s4, -164($fp)
s.s $f21, -316($fp)
s.s $f20, -320($fp)
sw $s3, -180($fp)
sw $s2, -304($fp)
sw $s1, -204($fp)
sw $s0, -220($fp)
_for_1:
lw $s7, -220($fp)
lw $s6, -304($fp)
sw $s7, -220($fp)
sw $s6, -304($fp)
move $t3, $s7
move $t2, $s6
bgt $t3, $t2, _after_for_2
l.s $f23, -416($fp)
l.s $f22, -252($fp)
l.s $f21, -256($fp)
l.s $f20, -260($fp)
lw $s7, -188($fp)
lw $s6, -200($fp)
l.s $f7, -432($fp)
l.s $f6, -444($fp)
sub.s $f5, $f7, $f6
mov.s $f20, $f5
mov.s $f7, $f20
l.s $f6, -412($fp)
div.s $f5, $f7, $f6
s.s $f5, -264($fp)
l.s $f6, -264($fp)
mov.s $f7, $f6
mov.s $f23, $f7
mov.s $f7, $f23
l.s $f6, -184($fp)
sub.s $f5, $f7, $f6
mov.s $f22, $f5
mov.s $f7, $f22
l.s $f6, -412($fp)
div.s $f5, $f7, $f6
mov.s $f21, $f5
mov.s $f6, $f21
mov.s $f7, $f6
s.s $f7, -280($fp)
l.s $f7, -480($fp)
l.s $f6, -300($fp)
mul.s $f5, $f7, $f6
s.s $f5, -292($fp)
l.s $f7, -396($fp)
mov.s $f6, $f23
mul.s $f5, $f7, $f6
s.s $f5, -296($fp)
l.s $f7, -404($fp)
l.s $f6, -296($fp)
add.s $f5, $f7, $f6
s.s $f5, -284($fp)
l.s $f7, -292($fp)
l.s $f6, -284($fp)
mul.s $f5, $f7, $f6
s.s $f5, -288($fp)
l.s $f6, -288($fp)
mov.s $f7, $f6
s.s $f7, -356($fp)
li.s $f6, 0.500000
mov.s $f7, $f6
s.s $f7, -228($fp)
l.s $f7, -228($fp)
l.s $f6, -216($fp)
mul.s $f5, $f7, $f6
s.s $f5, -212($fp)
l.s $f7, -212($fp)
l.s $f6, -308($fp)
mul.s $f5, $f7, $f6
s.s $f5, -224($fp)
li $t2, 2
move $t3, $t2
move $s6, $t3
li $t2, 0
move $t3, $t2
move $s7, $t3
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -208($fp)
s.s $f23, -416($fp)
s.s $f22, -252($fp)
s.s $f21, -256($fp)
s.s $f20, -260($fp)
sw $s7, -188($fp)
sw $s6, -200($fp)
_pow_3:
lw $s7, -188($fp)
lw $s6, -200($fp)
sw $s7, -188($fp)
sw $s6, -200($fp)
move $t3, $s7
move $t2, $s6
bge $t3, $t2, _after_pow_4
lw $s7, -188($fp)
l.s $f23, -208($fp)
l.s $f22, -416($fp)
mov.s $f7, $f23
mov.s $f6, $f22
mul.s $f5, $f7, $f6
mov.s $f23, $f5
move $t3, $s7
li $t2, 1
add $t1, $t3, $t2
move $s7, $t1
sw $s7, -188($fp)
s.s $f23, -208($fp)
s.s $f22, -416($fp)
j _pow_3
_after_pow_4:
l.s $f23, -192($fp)
l.s $f22, -152($fp)
l.s $f21, -232($fp)
l.s $f20, -236($fp)
lw $s7, -204($fp)
l.s $f7, -224($fp)
l.s $f6, -208($fp)
mul.s $f5, $f7, $f6
mov.s $f23, $f5
mov.s $f6, $f23
mov.s $f7, $f6
mov.s $f21, $f7
mov.s $f7, $f21
l.s $f6, -356($fp)
add.s $f5, $f7, $f6
s.s $f5, -240($fp)
l.s $f7, -480($fp)
l.s $f6, -280($fp)
mul.s $f5, $f7, $f6
s.s $f5, -244($fp)
l.s $f7, -240($fp)
l.s $f6, -244($fp)
add.s $f5, $f7, $f6
mov.s $f20, $f5
mov.s $f6, $f20
mov.s $f7, $f6
s.s $f7, -96($fp)
l.s $f7, -96($fp)
l.s $f6, -108($fp)
mul.s $f5, $f7, $f6
mov.s $f22, $f5
mov.s $f6, $f22
mov.s $f7, $f6
s.s $f7, -24($fp)
s.s $f23, -192($fp)
s.s $f22, -152($fp)
s.s $f21, -232($fp)
s.s $f20, -236($fp)
sw $s7, -204($fp)
move $t3, $s7
li $t2, 0
beq $t3, $t2, _if_false_do_this_5
lw $s7, -128($fp)
lw $s6, -136($fp)
l.s $f23, -144($fp)
l.s $f22, -160($fp)
li.s $f6, 0.011000
mov.s $f7, $f6
mov.s $f22, $f7
li $t2, 2
move $t3, $t2
move $s6, $t3
li $t2, 0
move $t3, $t2
move $s7, $t3
li.s $f6, 1.000000
mov.s $f7, $f6
mov.s $f23, $f7
sw $s7, -128($fp)
sw $s6, -136($fp)
s.s $f23, -144($fp)
s.s $f22, -160($fp)
_pow_6:
lw $s7, -128($fp)
lw $s6, -136($fp)
sw $s7, -128($fp)
sw $s6, -136($fp)
move $t3, $s7
move $t2, $s6
bge $t3, $t2, _after_pow_7
lw $s7, -128($fp)
l.s $f23, -144($fp)
l.s $f22, -196($fp)
mov.s $f7, $f23
mov.s $f6, $f22
mul.s $f5, $f7, $f6
mov.s $f23, $f5
move $t3, $s7
li $t2, 1
add $t1, $t3, $t2
move $s7, $t1
sw $s7, -128($fp)
s.s $f23, -144($fp)
s.s $f22, -196($fp)
j _pow_6
_after_pow_7:
l.s $f23, -276($fp)
l.s $f22, -104($fp)
l.s $f21, -116($fp)
l.s $f20, -132($fp)
lw $s7, -16($fp)
lw $s6, -28($fp)
l.s $f7, -160($fp)
l.s $f6, -144($fp)
mul.s $f5, $f7, $f6
mov.s $f20, $f5
li.s $f6, 0.003300
mov.s $f7, $f6
mov.s $f22, $f7
mov.s $f7, $f22
l.s $f6, -196($fp)
mul.s $f5, $f7, $f6
mov.s $f21, $f5
mov.s $f7, $f20
mov.s $f6, $f21
add.s $f5, $f7, $f6
s.s $f5, -172($fp)
li.s $f6, 0.020000
mov.s $f7, $f6
s.s $f7, -176($fp)
l.s $f7, -172($fp)
l.s $f6, -176($fp)
add.s $f5, $f7, $f6
s.s $f5, -100($fp)
l.s $f6, -100($fp)
mov.s $f7, $f6
s.s $f7, -92($fp)
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -80($fp)
l.s $f7, -92($fp)
l.s $f6, -52($fp)
div.s $f5, $f7, $f6
s.s $f5, -84($fp)
l.s $f7, -80($fp)
l.s $f6, -84($fp)
sub.s $f5, $f7, $f6
s.s $f5, -72($fp)
l.s $f6, -72($fp)
mov.s $f7, $f6
s.s $f7, -408($fp)
li.s $f6, 7.600000
mov.s $f7, $f6
s.s $f7, -76($fp)
li.s $f6, 79.000000
mov.s $f7, $f6
s.s $f7, -60($fp)
l.s $f7, -500($fp)
l.s $f6, -60($fp)
sub.s $f5, $f7, $f6
s.s $f5, -64($fp)
l.s $f7, -76($fp)
l.s $f6, -64($fp)
mul.s $f5, $f7, $f6
s.s $f5, -48($fp)
li.s $f6, 600.000000
mov.s $f7, $f6
s.s $f7, -56($fp)
l.s $f7, -48($fp)
l.s $f6, -56($fp)
add.s $f5, $f7, $f6
s.s $f5, -88($fp)
l.s $f6, -88($fp)
mov.s $f7, $f6
mov.s $f23, $f7
li.s $f6, 0.180000
mov.s $f7, $f6
s.s $f7, -40($fp)
l.s $f7, -40($fp)
l.s $f6, -24($fp)
mul.s $f5, $f7, $f6
s.s $f5, -44($fp)
mov.s $f7, $f23
l.s $f6, -44($fp)
add.s $f5, $f7, $f6
s.s $f5, -32($fp)
mov.s $f7, $f23
l.s $f6, -32($fp)
div.s $f5, $f7, $f6
s.s $f5, -36($fp)
l.s $f6, -36($fp)
mov.s $f7, $f6
s.s $f7, -484($fp)
l.s $f7, -52($fp)
l.s $f6, -500($fp)
div.s $f5, $f7, $f6
s.s $f5, -20($fp)
l.s $f6, -20($fp)
mov.s $f7, $f6
s.s $f7, -168($fp)
li $t2, 2
move $t3, $t2
move $s6, $t3
li $t2, 0
move $t3, $t2
move $s7, $t3
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -12($fp)
s.s $f23, -276($fp)
s.s $f22, -104($fp)
s.s $f21, -116($fp)
s.s $f20, -132($fp)
sw $s7, -16($fp)
sw $s6, -28($fp)
_pow_8:
lw $s7, -16($fp)
lw $s6, -28($fp)
sw $s7, -16($fp)
sw $s6, -28($fp)
move $t3, $s7
move $t2, $s6
bge $t3, $t2, _after_pow_9
l.s $f23, -12($fp)
lw $s7, -16($fp)
l.s $f22, -168($fp)
mov.s $f7, $f23
mov.s $f6, $f22
mul.s $f5, $f7, $f6
mov.s $f23, $f5
move $t3, $s7
li $t2, 1
add $t1, $t3, $t2
move $s7, $t1
s.s $f23, -12($fp)
sw $s7, -16($fp)
s.s $f22, -168($fp)
j _pow_8
_after_pow_9:
l.s $f23, -4($fp)
l.s $f22, -8($fp)
l.s $f21, -124($fp)
l.s $f20, -460($fp)
lw $s7, -456($fp)
lw $s6, -428($fp)
l.s $f7, -504($fp)
l.s $f6, -12($fp)
mul.s $f5, $f7, $f6
mov.s $f23, $f5
mov.s $f6, $f23
mov.s $f7, $f6
mov.s $f21, $f7
li.s $f6, 1.000000
mov.s $f7, $f6
mov.s $f22, $f7
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -496($fp)
mov.s $f7, $f21
l.s $f6, -52($fp)
div.s $f5, $f7, $f6
s.s $f5, -488($fp)
l.s $f7, -496($fp)
l.s $f6, -488($fp)
add.s $f5, $f7, $f6
s.s $f5, -492($fp)
mov.s $f7, $f22
l.s $f6, -492($fp)
div.s $f5, $f7, $f6
s.s $f5, -468($fp)
l.s $f6, -468($fp)
mov.s $f7, $f6
s.s $f7, -312($fp)
l.s $f7, -408($fp)
l.s $f6, -484($fp)
mul.s $f5, $f7, $f6
s.s $f5, -472($fp)
l.s $f7, -472($fp)
l.s $f6, -312($fp)
mul.s $f5, $f7, $f6
mov.s $f20, $f5
mov.s $f6, $f20
mov.s $f7, $f6
s.s $f7, -440($fp)
li.s $f6, 3.000000
mov.s $f7, $f6
s.s $f7, -464($fp)
l.s $f7, -464($fp)
l.s $f6, -476($fp)
mul.s $f5, $f7, $f6
s.s $f5, -452($fp)
li $t2, 2
move $t3, $t2
move $s7, $t3
li $t2, 0
move $t3, $t2
move $s6, $t3
li.s $f6, 1.000000
mov.s $f7, $f6
s.s $f7, -448($fp)
s.s $f23, -4($fp)
s.s $f22, -8($fp)
s.s $f21, -124($fp)
s.s $f20, -460($fp)
sw $s7, -456($fp)
sw $s6, -428($fp)
_pow_10:
lw $s7, -456($fp)
lw $s6, -428($fp)
sw $s7, -456($fp)
sw $s6, -428($fp)
move $t3, $s6
move $t2, $s7
bge $t3, $t2, _after_pow_11
l.s $f23, -448($fp)
lw $s7, -428($fp)
l.s $f22, -196($fp)
mov.s $f7, $f23
mov.s $f6, $f22
mul.s $f5, $f7, $f6
mov.s $f23, $f5
move $t3, $s7
li $t2, 1
add $t1, $t3, $t2
move $s7, $t1
s.s $f23, -448($fp)
sw $s7, -428($fp)
s.s $f22, -196($fp)
j _pow_10
_after_pow_11:
l.s $f23, -436($fp)
l.s $f22, -448($fp)
l.s $f21, -120($fp)
l.s $f20, -452($fp)
mov.s $f7, $f20
mov.s $f6, $f22
mul.s $f5, $f7, $f6
mov.s $f23, $f5
mov.s $f6, $f23
mov.s $f7, $f6
mov.s $f21, $f7
s.s $f23, -436($fp)
s.s $f22, -448($fp)
s.s $f21, -120($fp)
s.s $f20, -452($fp)
_if_false_do_this_5:
lw $s7, -180($fp)
sw $s7, -180($fp)
move $t3, $s7
li $t2, 0
beq $t3, $t2, _if_false_do_this_13
l.s $f23, -68($fp)
l.s $f22, -392($fp)
l.s $f21, -400($fp)
l.s $f20, -420($fp)
l.s $f7, -24($fp)
l.s $f6, -416($fp)
mul.s $f5, $f7, $f6
mov.s $f20, $f5
mov.s $f7, $f20
l.s $f6, -108($fp)
div.s $f5, $f7, $f6
s.s $f5, -424($fp)
l.s $f6, -424($fp)
mov.s $f7, $f6
mov.s $f23, $f7
mov.s $f7, $f23
l.s $f6, -440($fp)
mul.s $f5, $f7, $f6
mov.s $f22, $f5
mov.s $f7, $f22
l.s $f6, -120($fp)
add.s $f5, $f7, $f6
mov.s $f21, $f5
mov.s $f6, $f21
mov.s $f7, $f6
s.s $f7, -248($fp)
addiu $sp, $sp, 0
l.s $f7, -248($fp)
mov.s $f12, $f7
jal _fun_printf
addiu $sp, $sp, 0
s.s $f23, -68($fp)
s.s $f22, -392($fp)
s.s $f21, -400($fp)
s.s $f20, -420($fp)
j _after_if_do_this_12
_if_false_do_this_13:
l.s $f23, -24($fp)
addiu $sp, $sp, 0
mov.s $f7, $f23
mov.s $f12, $f7
jal _fun_printf
addiu $sp, $sp, 0
s.s $f23, -24($fp)
_after_if_do_this_12:
l.s $f23, -384($fp)
l.s $f22, -388($fp)
lw $s7, -220($fp)
l.s $f21, -500($fp)
li.s $f6, 1.000000
mov.s $f7, $f6
mov.s $f23, $f7
mov.s $f7, $f21
mov.s $f6, $f23
sub.s $f5, $f7, $f6
mov.s $f22, $f5
mov.s $f6, $f22
mov.s $f7, $f6
mov.s $f21, $f7
move $t3, $s7
li $t2, 1
add $t1, $t3, $t2
move $s7, $t1
s.s $f23, -384($fp)
s.s $f22, -388($fp)
sw $s7, -220($fp)
s.s $f21, -500($fp)
j _for_1
_after_for_2:
lw $s7, -112($fp)
li $t2, 0
move $t3, $t2
move $s7, $t3
sw $s7, -112($fp)
move $t3, $s7
move, $v0, $t3
lw $s0, -548($fp)
lw $s1, -544($fp)
lw $s2, -540($fp)
lw $s3, -536($fp)
lw $s4, -532($fp)
lw $s5, -528($fp)
lw $s6, -524($fp)
lw $s7, -520($fp)
l.s $f20, -516($fp)
l.s $f21, -512($fp)
l.s $f22, -508($fp)
l.s $f23, -504($fp)
lw $ra, -556($fp)
addiu $sp, $sp, 556
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $s0, -548($fp)
lw $s1, -544($fp)
lw $s2, -540($fp)
lw $s3, -536($fp)
lw $s4, -532($fp)
lw $s5, -528($fp)
lw $s6, -524($fp)
lw $s7, -520($fp)
l.s $f20, -516($fp)
l.s $f21, -512($fp)
l.s $f22, -508($fp)
l.s $f23, -504($fp)
lw $ra, -556($fp)
addiu $sp, $sp, 556
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra

_fun_printi:
li $v0, 1
syscall
li $v0, 11
li $a0, 10
syscall
jr $ra

_fun_printf:
li $v0, 2
syscall
li $v0, 11
li $a0, 10
syscall
jr $ra


_fun_not:
beq $a0, $zero, not0
not1:
move $v0, $zero
jr $ra
not0:
li $v0, 1
jr $ra


_fun_exit:
li $v0, 17
syscall
jr $ra
