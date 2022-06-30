.data
.text
.globl main
main:
_fun_main:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -52
sw $s0, -48($fp)
sw $s1, -44($fp)
sw $s2, -40($fp)
sw $s3, -36($fp)
sw $s4, -32($fp)
sw $s5, -28($fp)
sw $s6, -24($fp)
sw $s7, -20($fp)
s.s $f20, -16($fp)
s.s $f21, -12($fp)
s.s $f22, -8($fp)
s.s $f23, -4($fp)
sw $ra, -52($fp)
li $t2, 0
move $t3, $t2
move $s0, $t3
li $t2, 0
move $t3, $t2
move $s4, $t3
li $t2, 1
move $t3, $t2
move $s2, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
_while_1:
li $t2, 10
move $t3, $t2
move $s3, $t3
li $t2, 1
move $t3, $t2
move $s1, $t3
move $t3, $s4
move $t2, $s3
blt $t3, $t2, _cmp_op_3
li $t2, 0
move $t3, $t2
move $s1, $t3
_cmp_op_3:
move $t3, $s1
li $t2, 0
beq $t3, $t2, _after_while_2
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
li $t2, 5
move $t3, $t2
move $s1, $t3
li $t2, 1
move $t3, $t2
move $s3, $t3
move $t3, $s4
move $t2, $s1
blt $t3, $t2, _cmp_op_4
li $t2, 0
move $t3, $t2
move $s3, $t3
_cmp_op_4:
move $t3, $s3
li $t2, 0
beq $t3, $t2, _if_false_do_this_6
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
j _after_if_do_this_5
_if_false_do_this_6:
move $t3, $s0
move $t2, $s2
sub $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
sub $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
sub $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
_after_if_do_this_5:
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
move $t3, $s0
move $t2, $s2
add $t1, $t3, $t2
move $s1, $t1
move $t2, $s1
move $t3, $t2
move $s0, $t3
li $t2, 1
move $t3, $t2
move $s1, $t3
move $t3, $s4
move $t2, $s1
add $t1, $t3, $t2
move $s3, $t1
move $t2, $s3
move $t3, $t2
move $s4, $t3
j _while_1
_after_while_2:
addiu $sp, $sp, 0
move $t3, $s0
move $a0, $t3
jal _fun_printi
addiu $sp, $sp, 0
li $t2, 0
move $t3, $t2
move $s0, $t3
move $t3, $s0
move, $v0, $t3
lw $s0, -48($fp)
lw $s1, -44($fp)
lw $s2, -40($fp)
lw $s3, -36($fp)
lw $s4, -32($fp)
lw $s5, -28($fp)
lw $s6, -24($fp)
lw $s7, -20($fp)
l.s $f20, -16($fp)
l.s $f21, -12($fp)
l.s $f22, -8($fp)
l.s $f23, -4($fp)
lw $ra, -52($fp)
addiu $sp, $sp, 52
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $s0, -48($fp)
lw $s1, -44($fp)
lw $s2, -40($fp)
lw $s3, -36($fp)
lw $s4, -32($fp)
lw $s5, -28($fp)
lw $s6, -24($fp)
lw $s7, -20($fp)
l.s $f20, -16($fp)
l.s $f21, -12($fp)
l.s $f22, -8($fp)
l.s $f23, -4($fp)
lw $ra, -52($fp)
addiu $sp, $sp, 52
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
