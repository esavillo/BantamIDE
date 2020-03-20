      # File: include/whileloop.asm
      # S19 CS461 Project 19
      # Names: Martin Deutsch, Robert Durst, and Evan Savillo
      # Date: 4/11/2019
      
# _/--[ .data ]-------------------------------------------------------------
      .data                           	#
      .globl   gc_flag                	#
      .globl   class_name_table       	#

gc_flag:                             	#
      .word    0                      	#
      
# _/--[ String Constants ]--------------------------------------------------

StringConst_0:                       	#
      .word    1                      	#
      .word    24                     	#
      .word    String_dispatch_table  	#
      .word    5                      	#
      .asciiz  "Sum: "                	#
      .align   2                      	#

filename:                            	#
      .word    1                      	#
      .word    40                     	#
      .word    String_dispatch_table  	#
      .word    21                     	#
      .asciiz  "include/whileloop.asm"	#
      .align   2                      	#
      
# _/--[ Class Name Strings ]------------------------------------------------

class_name_0:                        	#
      .word    1                      	#
      .word    24                     	#
      .word    String_dispatch_table  	#
      .word    6                      	#
      .asciiz  "Object"               	#
      .align   2                      	#

class_name_1:                        	#
      .word    1                      	#
      .word    24                     	#
      .word    String_dispatch_table  	#
      .word    6                      	#
      .asciiz  "String"               	#
      .align   2                      	#

class_name_2:                        	#
      .word    1                      	#
      .word    20                     	#
      .word    String_dispatch_table  	#
      .word    3                      	#
      .asciiz  "Sys"                  	#
      .align   2                      	#

class_name_3:                        	#
      .word    1                      	#
      .word    24                     	#
      .word    String_dispatch_table  	#
      .word    4                      	#
      .asciiz  "Main"                 	#
      .align   2                      	#

class_name_4:                        	#
      .word    1                      	#
      .word    24                     	#
      .word    String_dispatch_table  	#
      .word    6                      	#
      .asciiz  "TextIO"               	#
      .align   2                      	#

class_name_table:                    	#
      .word    class_name_0           	#
      .word    class_name_1           	#
      .word    class_name_2           	#
      .word    class_name_3           	#
      .word    class_name_4           	#
      
# _/--[ Object Templates ]--------------------------------------------------
      .globl   Main_template          	#
      .globl   TextIO_template        	#
      .globl   Sys_template           	#
      .globl   String_template        	#
      .globl   Object_template        	#
      .globl   Array_template         	#
      .globl   Lambda_template        	#

# Object templates are written according to the following scheme:

# <classname>_template:
      #     .word <numeric identifier>
      #     .word <size in bytes>
      #     .word <dispatch table pointer>
      #     .word <field 0>
      #     ...
      #     .word <field n - 1>

String_template:                     	#
      .word    1                      	#
      .word    16                     	#
      .word    String_dispatch_table  	#
      .word    0                      	#

Object_template:                     	#
      .word    0                      	#
      .word    12                     	#
      .word    Object_dispatch_table  	#

Sys_template:                        	#
      .word    2                      	#
      .word    12                     	#
      .word    Sys_dispatch_table     	#

Main_template:                       	#
      .word    3                      	#
      .word    16                     	#
      .word    Main_dispatch_table    	#
      .word    0                      	#

TextIO_template:                     	#
      .word    4                      	#
      .word    20                     	#
      .word    TextIO_dispatch_table  	#
      .word    0                      	#
      .word    0                      	#

Array_template:                      	#
      .word    0                      	#
      .word    16                     	#
      .word    Object_dispatch_table  	#
      .word    0                      	#

Lambda_template:                     	#
      .word    0                      	#
      .word    16                     	#
      .word    Object_dispatch_table  	#
      .word    0                      	#
      .globl   Main_dispatch_table    	#
      .globl   TextIO_dispatch_table  	#
      .globl   Sys_dispatch_table     	#
      .globl   String_dispatch_table  	#
      .globl   Object_dispatch_table  	#
      
# _/--[ Dispatch Tables ]---------------------------------------------------

Main_dispatch_table:                 	#
      .word    Object.toString        	#
      .word    Object.equals          	#
      .word    Object.clone           	#
      .word    Main.main              	#
      .word    Main.lorem             	#

TextIO_dispatch_table:               	#
      .word    Object.toString        	#
      .word    Object.equals          	#
      .word    Object.clone           	#
      .word    TextIO.putInt          	#
      .word    TextIO.putString       	#
      .word    TextIO.getInt          	#
      .word    TextIO.getString       	#
      .word    TextIO.writeFile       	#
      .word    TextIO.writeStderr     	#
      .word    TextIO.writeStdout     	#
      .word    TextIO.readFile        	#
      .word    TextIO.readStdin       	#

Sys_dispatch_table:                  	#
      .word    Object.toString        	#
      .word    Object.equals          	#
      .word    Object.clone           	#
      .word    Sys.random             	#
      .word    Sys.time               	#
      .word    Sys.exit               	#

String_dispatch_table:               	#
      .word    String.toString        	#
      .word    String.equals          	#
      .word    Object.clone           	#
      .word    String.concat          	#
      .word    String.substring       	#
      .word    String.length          	#

Object_dispatch_table:               	#
      .word    Object.toString        	#
      .word    Object.equals          	#
      .word    Object.clone           	#
      
# _/--[ .text ]-------------------------------------------------------------
      .text                           	#
      .globl   main                   	#
      .globl   Main_init              	#
      .globl   Main.main              	#

main:                                	#
      jal      __start                	#

Main_init:                           	#
      la       $a2, filename          	# Load the filename into $a2 for error printing
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      jal      Object_init            	# Call parent init subroutine
      #     New expression begin
      # push $a0
      add      $sp, $sp, -4           	#
      sw       $a0, 0($sp)            	#
      la       $a0, TextIO_template   	# Create new TextIO object
      jal      Object.clone           	#
      move     $a0, $v0               	# Set $a0 = $v0
      jal      TextIO_init            	# Initialize new object
      move     $v0, $a0               	# Set $v0 = $a0
      # pop into $a0
      lw       $a0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      #     New expression end
      sw       $v0, 12($a0)           	# Store initial value of "out" field from $v0
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label0:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

TextIO_init:                         	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      li       $v0, 1                 	# Load constant integer 1 to $v0
      sw       $v0, 16($a0)           	# Store initial value of "writeFD" field from $v0
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label1:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

Sys_init:                            	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label2:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

String_init:                         	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label3:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

Object_init:                         	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label4:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

Array_init:                          	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, 0            	# Make space for 0 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      lw       $t0, 12($a0)           	# Load array length to $t0
      add      $t1, $a0, 16           	# Load address of first element to $t1

label5:                              	# Begin loop of storing zeros
      sw       $zero, 0($t1)          	#
      add      $t1, $t1, 4            	# Go to next element
      sub      $t0, $t0, 1            	# Decrement $t0
      beq      $t0, $zero, label6     	#
      b        label5                 	#

label6:                              	#
      move     $v0, $a0               	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label7:                              	#
      add      $sp, $fp, 0            	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

Main.lorem:                          	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, -4           	# Make space for 1 local vars
      # Copy parameter i to local vars area of stack
      lw       $t0, 12($fp)           	#
      sw       $t0, 0($fp)            	#
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      # 
      #     Epilog Begin

label8:                              	#
      add      $sp, $fp, 4            	# Remove space for 1 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $sp, $sp, 4            	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 

Main.main:                           	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4           	#
      sw       $ra, 0($sp)            	#
      # push $fp
      add      $sp, $sp, -4           	#
      sw       $fp, 0($sp)            	#
      add      $fp, $sp, -8           	# Make space for 2 local vars
      move     $sp, $fp               	# Initialize stack pointer
      #     Prolog End
      # 
      #     Declaration statement begin
      li       $v0, 0                 	# Load constant integer 0 to $v0
      sw       $v0, 0($fp)            	# Store the variable "sum"
      #     Declaration statement end
      #     Declaration statement begin
      li       $v0, 0                 	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)            	# Store the variable "i"
      #     Declaration statement end
      # for_initialization
      li       $v0, 0                 	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)            	# Assign 4($fp) = $v0

label10:                             	# for_statement_begin (unrolled)
      # for_body entered
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      move     $v1, $v0               	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $v0, $v0, $v1          	# v0 + v1
      sw       $v0, 0($fp)            	# Assign 0($fp) = $v0
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      add      $t0, $v0, 1            	# Increment contents of $v0
      sw       $t0, 4($fp)            	# Store the incremented result
      move     $v0, $t0               	# Store modified value back in v0
      #     End unary expression
      # for_body entered
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      move     $v1, $v0               	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $v0, $v0, $v1          	# v0 + v1
      sw       $v0, 0($fp)            	# Assign 0($fp) = $v0
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      add      $t0, $v0, 1            	# Increment contents of $v0
      sw       $t0, 4($fp)            	# Store the incremented result
      move     $v0, $t0               	# Store modified value back in v0
      #     End unary expression
      # for_body entered
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      move     $v1, $v0               	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $v0, $v0, $v1          	# v0 + v1
      sw       $v0, 0($fp)            	# Assign 0($fp) = $v0
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      add      $t0, $v0, 1            	# Increment contents of $v0
      sw       $t0, 4($fp)            	# Store the incremented result
      move     $v0, $t0               	# Store modified value back in v0
      #     End unary expression
      # for_body entered
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      move     $v1, $v0               	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $v0, $v0, $v1          	# v0 + v1
      sw       $v0, 0($fp)            	# Assign 0($fp) = $v0
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      add      $t0, $v0, 1            	# Increment contents of $v0
      sw       $t0, 4($fp)            	# Store the incremented result
      move     $v0, $t0               	# Store modified value back in v0
      #     End unary expression
      # for_body entered
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      move     $v1, $v0               	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $v0, $v0, $v1          	# v0 + v1
      sw       $v0, 0($fp)            	# Assign 0($fp) = $v0
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)            	#
      #     End VarExpr
      add      $t0, $v0, 1            	# Increment contents of $v0
      sw       $t0, 4($fp)            	# Store the incremented result
      move     $v0, $t0               	# Store modified value back in v0
      #     End unary expression

label11:                             	# for_statement_body_end (unrolled)
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4           	#
      sw       $a0, 0($sp)            	#
      la       $v0, StringConst_0     	# Load constant String "Sum: " to $v0
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     Begin VarExpr
      #     node: [VarExpr: (this)]
      move     $v0, $a0               	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label12    	#
      b        label13                	#

label12:                             	#
      jal      _null_pointer_error    	#

label13:                             	#
      #     node: [VarExpr: (out)]
      lw       $v0, 12($v0)           	# Load value of 'out' from 'Main' to v0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label14    	#
      b        label15                	#

label14:                             	#
      jal      _null_pointer_error    	#

label15:                             	#
      move     $a0, $v0               	# Set $a0 = $v0
      lw       $t0, 8($a0)            	# Load address of vft to $t0
      lw       $t0, 16($t0)           	# Load address of desired method to $t0
      jalr     $t0                    	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4           	#
      sw       $a0, 0($sp)            	#
      #     Begin VarExpr
      #     node: [VarExpr: (sum)]
      # Load value of "sum" to v0
      lw       $v0, 0($fp)            	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4           	#
      sw       $v0, 0($sp)            	#
      #     Begin VarExpr
      #     Begin VarExpr
      #     node: [VarExpr: (this)]
      move     $v0, $a0               	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label16    	#
      b        label17                	#

label16:                             	#
      jal      _null_pointer_error    	#

label17:                             	#
      #     node: [VarExpr: (out)]
      lw       $v0, 12($v0)           	# Load value of 'out' from 'Main' to v0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label18    	#
      b        label19                	#

label18:                             	#
      jal      _null_pointer_error    	#

label19:                             	#
      move     $a0, $v0               	# Set $a0 = $v0
      lw       $t0, 8($a0)            	# Load address of vft to $t0
      lw       $t0, 12($t0)           	# Load address of desired method to $t0
      jalr     $t0                    	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      #     Dispatch expression end
      # 
      #     Epilog Begin

label9:                              	#
      add      $sp, $fp, 8            	# Remove space for 2 local vars
      # pop into $fp
      lw       $fp, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      # pop into $ra
      lw       $ra, 0($sp)            	#
      sub      $sp, $sp, -4           	#
      add      $sp, $sp, 0            	#
      jr       $ra                    	# Return to caller
      #     Epilog End
      # 
