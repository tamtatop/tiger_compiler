.data
.text
_fun_sum:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -140
sw $ra, -140($fp)
move $t3, $a0
sw $t3, -60($fp)
move $t3, $a1
sw $t3, -56($fp)
move $t3, $a2
sw $t3, -52($fp)
move $t3, $a3
sw $t3, -48($fp)
lw $t3, 4($fp)
sw $t3, -40($fp)
lw $t3, 8($fp)
sw $t3, -32($fp)
lw $t3, 12($fp)
sw $t3, -28($fp)
lw $t3, 16($fp)
sw $t3, -24($fp)
lw $t3, -60($fp)
lw $t2, -56($fp)
add $t1, $t3, $t2
sw $t1, -4($fp)
lw $t3, -4($fp)
lw $t2, -52($fp)
add $t1, $t3, $t2
sw $t1, -20($fp)
lw $t3, -20($fp)
lw $t2, -48($fp)
add $t1, $t3, $t2
sw $t1, -16($fp)
lw $t3, -16($fp)
lw $t2, -40($fp)
add $t1, $t3, $t2
sw $t1, -12($fp)
lw $t3, -12($fp)
lw $t2, -32($fp)
add $t1, $t3, $t2
sw $t1, -8($fp)
lw $t3, -8($fp)
lw $t2, -28($fp)
add $t1, $t3, $t2
sw $t1, -44($fp)
lw $t3, -44($fp)
lw $t2, -24($fp)
add $t1, $t3, $t2
sw $t1, -36($fp)
lw $t3, -36($fp)
move, $v0, $t3
lw $ra, -140($fp)
addiu $sp, $sp, 140
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $ra, -140($fp)
addiu $sp, $sp, 140
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
.globl main
main:
_fun_main:
addiu $sp, $sp, -4
sw $fp, 0($sp)
move $fp, $sp
addiu $sp, $sp, -124
sw $ra, -124($fp)
li $t2, 1
move $t3, $t2
sw $t3, -12($fp)
li $t2, 2
move $t3, $t2
sw $t3, -8($fp)
li $t2, 3
move $t3, $t2
sw $t3, -4($fp)
li $t2, 4
move $t3, $t2
sw $t3, -44($fp)
li $t2, 5
move $t3, $t2
sw $t3, -40($fp)
li $t2, 6
move $t3, $t2
sw $t3, -36($fp)
li $t2, 7
move $t3, $t2
sw $t3, -28($fp)
li $t2, 8
move $t3, $t2
sw $t3, -24($fp)
addiu $sp, $sp, -16
lw $t3, -12($fp)
move $a0, $t3
lw $t3, -8($fp)
move $a1, $t3
lw $t3, -4($fp)
move $a2, $t3
lw $t3, -44($fp)
move $a3, $t3
lw $t3, -40($fp)
sw $t3, -128($fp)
lw $t3, -36($fp)
sw $t3, -132($fp)
lw $t3, -28($fp)
sw $t3, -136($fp)
lw $t3, -24($fp)
sw $t3, -140($fp)
jal _fun_sum
addiu $sp, $sp, 16
move $t3, $v0
sw $t3, -20($fp)
lw $t2, -20($fp)
move $t3, $t2
sw $t3, -16($fp)
addiu $sp, $sp, 0
lw $t3, -16($fp)
move $a0, $t3
jal _fun_printi
addiu $sp, $sp, 0
li $t2, 0
move $t3, $t2
sw $t3, -32($fp)
lw $t3, -32($fp)
move, $v0, $t3
lw $ra, -124($fp)
addiu $sp, $sp, 124
lw $fp, 0($sp)
addiu $sp, $sp, 4
jr $ra
lw $ra, -124($fp)
addiu $sp, $sp, 124
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
