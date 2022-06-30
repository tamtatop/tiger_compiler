.data
.text
.globl main
main:
_fun_main:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -152
sw $s0, -148($fp)
sw $s1, -144($fp)
sw $s2, -140($fp)
sw $s3, -136($fp)
sw $s4, -132($fp)
sw $s5, -128($fp)
sw $s6, -124($fp)
sw $s7, -120($fp)
s.s $f20, -116($fp)
s.s $f21, -112($fp)
s.s $f22, -108($fp)
s.s $f23, -104($fp)
sw $ra, -152($fp)
lw $s7, -100($fp)
lw $s6, -92($fp)
lw $s5, -12($fp)
lw $s4, -16($fp)
lw $s3, -24($fp)
lw $s2, -68($fp)
li $t2, 0
move $t3, $t2
move $s7, $t3
li $t2, 0
move $t3, $t2
move $s2, $t3
li $t2, 1
move $t3, $t2
move $s6, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s4, $t1
move $t2, $s4
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s5, $t1
move $t2, $s5
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s3, $t1
move $t2, $s3
move $t3, $t2
move $s7, $t3
sw $s7, -100($fp)
sw $s6, -92($fp)
sw $s5, -12($fp)
sw $s4, -16($fp)
sw $s3, -24($fp)
sw $s2, -68($fp)
_while_1:
lw $s7, -20($fp)
lw $s6, -68($fp)
lw $s5, -76($fp)
li $t2, 10
move $t3, $t2
move $s7, $t3
li $t2, 1
move $t3, $t2
move $s5, $t3
sw $s7, -20($fp)
sw $s6, -68($fp)
sw $s5, -76($fp)
move $t3, $s6
move $t2, $s7
blt $t3, $t2, _cmp_op_3
lw $s7, -76($fp)
li $t2, 0
move $t3, $t2
move $s7, $t3
sw $s7, -76($fp)
_cmp_op_3:
lw $s7, -76($fp)
sw $s7, -76($fp)
move $t3, $s7
li $t2, 0
beq $t3, $t2, _after_while_2
lw $s7, -100($fp)
lw $s6, -92($fp)
lw $s5, -72($fp)
lw $s4, -80($fp)
lw $s3, -84($fp)
lw $s2, -96($fp)
lw $s1, -68($fp)
lw $s0, -88($fp)
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s5, $t1
move $t2, $s5
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s3, $t1
move $t2, $s3
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s4, $t1
move $t2, $s4
move $t3, $t2
move $s7, $t3
li $t2, 5
move $t3, $t2
move $s2, $t3
li $t2, 1
move $t3, $t2
move $s0, $t3
sw $s7, -100($fp)
sw $s6, -92($fp)
sw $s5, -72($fp)
sw $s4, -80($fp)
sw $s3, -84($fp)
sw $s2, -96($fp)
sw $s1, -68($fp)
sw $s0, -88($fp)
move $t3, $s1
move $t2, $s2
blt $t3, $t2, _cmp_op_4
lw $s7, -88($fp)
li $t2, 0
move $t3, $t2
move $s7, $t3
sw $s7, -88($fp)
_cmp_op_4:
lw $s7, -88($fp)
sw $s7, -88($fp)
move $t3, $s7
li $t2, 0
beq $t3, $t2, _if_false_do_this_6
lw $s7, -100($fp)
lw $s6, -92($fp)
lw $s5, -36($fp)
lw $s4, -40($fp)
lw $s3, -44($fp)
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s3, $t1
move $t2, $s3
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s5, $t1
move $t2, $s5
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s4, $t1
move $t2, $s4
move $t3, $t2
move $s7, $t3
sw $s7, -100($fp)
sw $s6, -92($fp)
sw $s5, -36($fp)
sw $s4, -40($fp)
sw $s3, -44($fp)
j _after_if_do_this_5
_if_false_do_this_6:
lw $s7, -100($fp)
lw $s6, -92($fp)
lw $s5, -28($fp)
lw $s4, -32($fp)
lw $s3, -60($fp)
move $t3, $s7
move $t2, $s6
sub $t1, $t3, $t2
move $s5, $t1
move $t2, $s5
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
sub $t1, $t3, $t2
move $s4, $t1
move $t2, $s4
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
sub $t1, $t3, $t2
move $s3, $t1
move $t2, $s3
move $t3, $t2
move $s7, $t3
sw $s7, -100($fp)
sw $s6, -92($fp)
sw $s5, -28($fp)
sw $s4, -32($fp)
sw $s3, -60($fp)
_after_if_do_this_5:
lw $s7, -100($fp)
lw $s6, -92($fp)
lw $s5, -8($fp)
lw $s4, -68($fp)
lw $s3, -48($fp)
lw $s2, -52($fp)
lw $s1, -56($fp)
lw $s0, -64($fp)
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s0, $t1
move $t2, $s0
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s2, $t1
move $t2, $s2
move $t3, $t2
move $s7, $t3
move $t3, $s7
move $t2, $s6
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s7, $t3
li $t2, 1
move $t3, $t2
move $s3, $t3
move $t3, $s4
move $t2, $s3
add $t1, $t3, $t2
move $s5, $t1
move $t2, $s5
move $t3, $t2
move $s4, $t3
sw $s7, -100($fp)
sw $s6, -92($fp)
sw $s5, -8($fp)
sw $s4, -68($fp)
sw $s3, -48($fp)
sw $s2, -52($fp)
sw $s1, -56($fp)
sw $s0, -64($fp)
j _while_1
_after_while_2:
lw $s7, -4($fp)
lw $s6, -100($fp)
addiu $sp, $sp, 0
move $t3, $s6
move $a0, $t3
jal _fun_printi
addiu $sp, $sp, 0
li $t2, 0
move $t3, $t2
move $s7, $t3
sw $s7, -4($fp)
sw $s6, -100($fp)
move $t3, $s7
move, $v0, $t3
lw $s0, -148($fp)
lw $s1, -144($fp)
lw $s2, -140($fp)
lw $s3, -136($fp)
lw $s4, -132($fp)
lw $s5, -128($fp)
lw $s6, -124($fp)
lw $s7, -120($fp)
l.s $f20, -116($fp)
l.s $f21, -112($fp)
l.s $f22, -108($fp)
l.s $f23, -104($fp)
lw $ra, -152($fp)
addiu $sp, $sp, 152
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $s0, -148($fp)
lw $s1, -144($fp)
lw $s2, -140($fp)
lw $s3, -136($fp)
lw $s4, -132($fp)
lw $s5, -128($fp)
lw $s6, -124($fp)
lw $s7, -120($fp)
l.s $f20, -116($fp)
l.s $f21, -112($fp)
l.s $f22, -108($fp)
l.s $f23, -104($fp)
lw $ra, -152($fp)
addiu $sp, $sp, 152
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
