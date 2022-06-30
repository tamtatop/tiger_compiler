.data
.text
.globl main
main:
_fun_main:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -104
sw $ra, -104($fp)
li $t2, 0
move $t3, $t2
sw $t3, -100($fp)
li $t2, 0
move $t3, $t2
sw $t3, -68($fp)
li $t2, 1
move $t3, $t2
sw $t3, -92($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -16($fp)
lw $t2, -16($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -12($fp)
lw $t2, -12($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -24($fp)
lw $t2, -24($fp)
move $t3, $t2
sw $t3, -100($fp)
_while_1:
li $t2, 10
move $t3, $t2
sw $t3, -20($fp)
li $t2, 1
move $t3, $t2
sw $t3, -76($fp)
lw $t3, -68($fp)
lw $t2, -20($fp)
blt $t3, $t2, _cmp_op_3
li $t2, 0
move $t3, $t2
sw $t3, -76($fp)
_cmp_op_3:
lw $t3, -76($fp)
li $t2, 0
beq $t3, $t2, _after_while_2
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -72($fp)
lw $t2, -72($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -84($fp)
lw $t2, -84($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -80($fp)
lw $t2, -80($fp)
move $t3, $t2
sw $t3, -100($fp)
li $t2, 5
move $t3, $t2
sw $t3, -96($fp)
li $t2, 1
move $t3, $t2
sw $t3, -88($fp)
lw $t3, -68($fp)
lw $t2, -96($fp)
blt $t3, $t2, _cmp_op_4
li $t2, 0
move $t3, $t2
sw $t3, -88($fp)
_cmp_op_4:
lw $t3, -88($fp)
li $t2, 0
beq $t3, $t2, _if_false_do_this_6
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -44($fp)
lw $t2, -44($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -36($fp)
lw $t2, -36($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -40($fp)
lw $t2, -40($fp)
move $t3, $t2
sw $t3, -100($fp)
j _after_if_do_this_5
_if_false_do_this_6:
lw $t3, -100($fp)
lw $t2, -92($fp)
sub $t1, $t3, $t2
sw $t1, -28($fp)
lw $t2, -28($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
sub $t1, $t3, $t2
sw $t1, -32($fp)
lw $t2, -32($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
sub $t1, $t3, $t2
sw $t1, -60($fp)
lw $t2, -60($fp)
move $t3, $t2
sw $t3, -100($fp)
_after_if_do_this_5:
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -64($fp)
lw $t2, -64($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -52($fp)
lw $t2, -52($fp)
move $t3, $t2
sw $t3, -100($fp)
lw $t3, -100($fp)
lw $t2, -92($fp)
add $t1, $t3, $t2
sw $t1, -56($fp)
lw $t2, -56($fp)
move $t3, $t2
sw $t3, -100($fp)
li $t2, 1
move $t3, $t2
sw $t3, -48($fp)
lw $t3, -68($fp)
lw $t2, -48($fp)
add $t1, $t3, $t2
sw $t1, -8($fp)
lw $t2, -8($fp)
move $t3, $t2
sw $t3, -68($fp)
j _while_1
_after_while_2:
addiu $sp, $sp, 0
lw $t3, -100($fp)
move $a0, $t3
jal _fun_printi
addiu $sp, $sp, 0
li $t2, 0
move $t3, $t2
sw $t3, -4($fp)
lw $t3, -4($fp)
move, $v0, $t3
lw $ra, -104($fp)
addiu $sp, $sp, 104
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $ra, -104($fp)
addiu $sp, $sp, 104
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
