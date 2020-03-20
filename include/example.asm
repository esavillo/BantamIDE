      # File: include/example.asm
      # S19 CS461 Project 19
      # Names: Martin Deutsch, Robert Durst, and Evan Savillo
      # Date: 4/11/2019
      
# _/--[ .data ]-------------------------------------------------------------
      .data                                                                                                                                          	#
      .globl   gc_flag                                                                                                                               	#
      .globl   class_name_table                                                                                                                      	#

gc_flag:                                                                                                                                            	#
      .word    0                                                                                                                                     	#
      
# _/--[ String Constants ]--------------------------------------------------

StringConst_0:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    6                                                                                                                                     	#
      .ascii   ""                                                                                                                                    	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  "    "                                                                                                                                	#
      .align   2                                                                                                                                     	#

StringConst_12:                                                                                                                                     	#
      .word    1                                                                                                                                     	#
      .word    124                                                                                                                                   	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    107                                                                                                                                   	#
      .ascii   "Some people say that there is nothing better to do than code functionally. With a lisp I said "                                      	#
      .byte    0x22                                                                                                                                  	#
      .ascii   "I agree"                                                                                                                             	#
      .byte    0x22                                                                                                                                  	#
      .ascii   ""                                                                                                                                    	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_2:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    40                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    21                                                                                                                                    	#
      .ascii   "   this is expected"                                                                                                                 	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_8:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    36                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    18                                                                                                                                    	#
      .asciiz  "   eat more chikun"                                                                                                                  	#
      .align   2                                                                                                                                     	#

StringConst_7:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    20                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    3                                                                                                                                     	#
      .asciiz  "   "                                                                                                                                 	#
      .align   2                                                                                                                                     	#

StringConst_13:                                                                                                                                     	#
      .word    1                                                                                                                                     	#
      .word    60                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    43                                                                                                                                    	#
      .ascii   "   it was not not not the worst of times."                                                                                           	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_6:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    152                                                                                                                                   	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    134                                                                                                                                   	#
      .ascii   "   It is a far, far better thing that I do, than I have ever done; it is a far, far better rest that I go to than I have ever known."	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_11:                                                                                                                                     	#
      .word    1                                                                                                                                     	#
      .word    60                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    43                                                                                                                                    	#
      .ascii   "   if this executes, then Windows > Linux"                                                                                           	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_3:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    72                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    53                                                                                                                                    	#
      .ascii   "this is not the print statement you are looking for"                                                                                 	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_1:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    5                                                                                                                                     	#
      .ascii   ""                                                                                                                                    	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  "   "                                                                                                                                 	#
      .align   2                                                                                                                                     	#

StringConst_4:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    44                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    26                                                                                                                                    	#
      .ascii   "   this is also expected"                                                                                                            	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_9:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    36                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    16                                                                                                                                    	#
      .asciiz  "   eat more cows"                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_10:                                                                                                                                     	#
      .word    1                                                                                                                                     	#
      .word    48                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    30                                                                                                                                    	#
      .ascii   "   it was the best of times."                                                                                                        	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

StringConst_5:                                                                                                                                      	#
      .word    1                                                                                                                                     	#
      .word    60                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    41                                                                                                                                    	#
      .ascii   "   honestly, this is even more expected"                                                                                             	#
      .byte    0xA                                                                                                                                   	#
      .asciiz  ""                                                                                                                                    	#
      .align   2                                                                                                                                     	#

filename:                                                                                                                                           	#
      .word    1                                                                                                                                     	#
      .word    36                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    19                                                                                                                                    	#
      .asciiz  "include/example.asm"                                                                                                                 	#
      .align   2                                                                                                                                     	#
      
# _/--[ Class Name Strings ]------------------------------------------------

class_name_0:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    6                                                                                                                                     	#
      .asciiz  "Object"                                                                                                                              	#
      .align   2                                                                                                                                     	#

class_name_1:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    6                                                                                                                                     	#
      .asciiz  "String"                                                                                                                              	#
      .align   2                                                                                                                                     	#

class_name_2:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    20                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    3                                                                                                                                     	#
      .asciiz  "Sys"                                                                                                                                 	#
      .align   2                                                                                                                                     	#

class_name_3:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    20                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    3                                                                                                                                     	#
      .asciiz  "Foo"                                                                                                                                 	#
      .align   2                                                                                                                                     	#

class_name_4:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    4                                                                                                                                     	#
      .asciiz  "Main"                                                                                                                                	#
      .align   2                                                                                                                                     	#

class_name_5:                                                                                                                                       	#
      .word    1                                                                                                                                     	#
      .word    24                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    6                                                                                                                                     	#
      .asciiz  "TextIO"                                                                                                                              	#
      .align   2                                                                                                                                     	#

class_name_table:                                                                                                                                   	#
      .word    class_name_0                                                                                                                          	#
      .word    class_name_1                                                                                                                          	#
      .word    class_name_2                                                                                                                          	#
      .word    class_name_3                                                                                                                          	#
      .word    class_name_4                                                                                                                          	#
      .word    class_name_5                                                                                                                          	#
      
# _/--[ Object Templates ]--------------------------------------------------
      .globl   Main_template                                                                                                                         	#
      .globl   TextIO_template                                                                                                                       	#
      .globl   Foo_template                                                                                                                          	#
      .globl   Sys_template                                                                                                                          	#
      .globl   String_template                                                                                                                       	#
      .globl   Object_template                                                                                                                       	#
      .globl   Array_template                                                                                                                        	#
      .globl   Lambda_template                                                                                                                       	#

# Object templates are written according to the following scheme:

# <classname>_template:
      #     .word <numeric identifier>
      #     .word <size in bytes>
      #     .word <dispatch table pointer>
      #     .word <field 0>
      #     ...
      #     .word <field n - 1>

String_template:                                                                                                                                    	#
      .word    1                                                                                                                                     	#
      .word    16                                                                                                                                    	#
      .word    String_dispatch_table                                                                                                                 	#
      .word    0                                                                                                                                     	#

Object_template:                                                                                                                                    	#
      .word    0                                                                                                                                     	#
      .word    12                                                                                                                                    	#
      .word    Object_dispatch_table                                                                                                                 	#

Sys_template:                                                                                                                                       	#
      .word    2                                                                                                                                     	#
      .word    12                                                                                                                                    	#
      .word    Sys_dispatch_table                                                                                                                    	#

Foo_template:                                                                                                                                       	#
      .word    3                                                                                                                                     	#
      .word    16                                                                                                                                    	#
      .word    Foo_dispatch_table                                                                                                                    	#
      .word    0                                                                                                                                     	#

Main_template:                                                                                                                                      	#
      .word    4                                                                                                                                     	#
      .word    28                                                                                                                                    	#
      .word    Main_dispatch_table                                                                                                                   	#
      .word    0                                                                                                                                     	#
      .word    0                                                                                                                                     	#
      .word    0                                                                                                                                     	#
      .word    0                                                                                                                                     	#

TextIO_template:                                                                                                                                    	#
      .word    5                                                                                                                                     	#
      .word    20                                                                                                                                    	#
      .word    TextIO_dispatch_table                                                                                                                 	#
      .word    0                                                                                                                                     	#
      .word    0                                                                                                                                     	#

Array_template:                                                                                                                                     	#
      .word    0                                                                                                                                     	#
      .word    16                                                                                                                                    	#
      .word    Object_dispatch_table                                                                                                                 	#
      .word    0                                                                                                                                     	#

Lambda_template:                                                                                                                                    	#
      .word    0                                                                                                                                     	#
      .word    16                                                                                                                                    	#
      .word    Object_dispatch_table                                                                                                                 	#
      .word    0                                                                                                                                     	#
      .globl   Main_dispatch_table                                                                                                                   	#
      .globl   TextIO_dispatch_table                                                                                                                 	#
      .globl   Foo_dispatch_table                                                                                                                    	#
      .globl   Sys_dispatch_table                                                                                                                    	#
      .globl   String_dispatch_table                                                                                                                 	#
      .globl   Object_dispatch_table                                                                                                                 	#
      
# _/--[ Dispatch Tables ]---------------------------------------------------

Main_dispatch_table:                                                                                                                                	#
      .word    Object.toString                                                                                                                       	#
      .word    Object.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      .word    Main.foo                                                                                                                              	#
      .word    Main.main                                                                                                                             	#

TextIO_dispatch_table:                                                                                                                              	#
      .word    Object.toString                                                                                                                       	#
      .word    Object.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      .word    TextIO.putInt                                                                                                                         	#
      .word    TextIO.putString                                                                                                                      	#
      .word    TextIO.getInt                                                                                                                         	#
      .word    TextIO.getString                                                                                                                      	#
      .word    TextIO.writeFile                                                                                                                      	#
      .word    TextIO.writeStderr                                                                                                                    	#
      .word    TextIO.writeStdout                                                                                                                    	#
      .word    TextIO.readFile                                                                                                                       	#
      .word    TextIO.readStdin                                                                                                                      	#

Foo_dispatch_table:                                                                                                                                 	#
      .word    Object.toString                                                                                                                       	#
      .word    Object.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      .word    Foo.foo                                                                                                                               	#

Sys_dispatch_table:                                                                                                                                 	#
      .word    Object.toString                                                                                                                       	#
      .word    Object.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      .word    Sys.random                                                                                                                            	#
      .word    Sys.time                                                                                                                              	#
      .word    Sys.exit                                                                                                                              	#

String_dispatch_table:                                                                                                                              	#
      .word    String.toString                                                                                                                       	#
      .word    String.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      .word    String.concat                                                                                                                         	#
      .word    String.substring                                                                                                                      	#
      .word    String.length                                                                                                                         	#

Object_dispatch_table:                                                                                                                              	#
      .word    Object.toString                                                                                                                       	#
      .word    Object.equals                                                                                                                         	#
      .word    Object.clone                                                                                                                          	#
      
# _/--[ .text ]-------------------------------------------------------------
      .text                                                                                                                                          	#
      .globl   main                                                                                                                                  	#
      .globl   Main_init                                                                                                                             	#
      .globl   Main.main                                                                                                                             	#

main:                                                                                                                                               	#
      jal      __start                                                                                                                               	#

Main_init:                                                                                                                                          	#
      la       $a2, filename                                                                                                                         	# Load the filename into $a2 for error printing
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      jal      Foo_init                                                                                                                              	# Call parent init subroutine
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label0:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

TextIO_init:                                                                                                                                        	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      li       $v0, 1                                                                                                                                	# Load constant integer 1 to $v0
      sw       $v0, 16($a0)                                                                                                                          	# Store initial value of "writeFD" field from $v0
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label1:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Foo_init:                                                                                                                                           	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      jal      Object_init                                                                                                                           	# Call parent init subroutine
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label2:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Sys_init:                                                                                                                                           	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label3:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

String_init:                                                                                                                                        	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label4:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Object_init:                                                                                                                                        	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label5:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Array_init:                                                                                                                                         	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      lw       $t0, 12($a0)                                                                                                                          	# Load array length to $t0
      add      $t1, $a0, 16                                                                                                                          	# Load address of first element to $t1

label6:                                                                                                                                             	# Begin loop of storing zeros
      sw       $zero, 0($t1)                                                                                                                         	#
      add      $t1, $t1, 4                                                                                                                           	# Go to next element
      sub      $t0, $t0, 1                                                                                                                           	# Decrement $t0
      beq      $t0, $zero, label7                                                                                                                    	#
      b        label6                                                                                                                                	#

label7:                                                                                                                                             	#
      move     $v0, $a0                                                                                                                              	# Move pointer to new object from $a0 to $v0
      # 
      #     Epilog Begin

label8:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Main.foo:                                                                                                                                           	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      li       $v0, 2                                                                                                                                	# Load constant integer 2 to $v0
      b        label9                                                                                                                                	# Jump to method epilog
      # 
      #     Epilog Begin

label9:                                                                                                                                             	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $sp, $sp, 0                                                                                                                           	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Main.main:                                                                                                                                          	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, -32                                                                                                                         	# Make space for 8 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      #     Declaration statement begin
      #     New expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $a0, TextIO_template                                                                                                                  	# Create new TextIO object
      jal      Object.clone                                                                                                                          	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      jal      TextIO_init                                                                                                                           	# Initialize new object
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     New expression end
      sw       $v0, 0($fp)                                                                                                                           	# Store the variable "io"
      #     Declaration statement end
      #     Declaration statement begin
      li       $v0, 1                                                                                                                                	# Load constant integer 1 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Store the variable "i"
      #     Declaration statement end
      li       $v0, 2                                                                                                                                	# Load constant integer 2 to $v0
      sw       $v0, 24($a0)                                                                                                                          	# Assign 24($a0) = $v0
      li       $v0, 3                                                                                                                                	# Load constant integer 3 to $v0
      sw       $v0, 12($a0)                                                                                                                          	# Assign 12($a0) = $v0
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label11                                                                                                                   	#
      b        label12                                                                                                                               	#

label11:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label12:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     Begin VarExpr
      #     node: [VarExpr: (this)]
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label13                                                                                                                   	#
      b        label14                                                                                                                               	#

label13:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label14:                                                                                                                                            	#
      #     node: [VarExpr: (i)]
      lw       $v0, 24($v0)                                                                                                                          	# Load value of 'i' from 'Main' to v0
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label15                                                                                                                   	#
      b        label16                                                                                                                               	#

label15:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label16:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     Begin VarExpr
      #     node: [VarExpr: (super)]
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label17                                                                                                                   	#
      b        label18                                                                                                                               	#

label17:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label18:                                                                                                                                            	#
      #     node: [VarExpr: (i)]
      lw       $v0, 12($v0)                                                                                                                          	# Load value of 'i' from 'Foo' to v0
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label19                                                                                                                   	#
      b        label20                                                                                                                               	#

label19:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label20:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Declaration statement begin
      li       $v0, 2                                                                                                                                	# Load constant integer 2 to $v0
      sw       $v0, 8($fp)                                                                                                                           	# Store the variable "lorem"
      #     Declaration statement end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      sw       $v0, 8($fp)                                                                                                                           	# Assign 8($fp) = $v0
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_0                                                                                                                    	# Load constant String "\n    " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label21                                                                                                                   	#
      b        label22                                                                                                                               	#

label21:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label22:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (lorem)]
      # Load value of "lorem" to v0
      lw       $v0, 8($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label23                                                                                                                   	#
      b        label24                                                                                                                               	#

label23:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label24:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (this)]
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label25                                                                                                                   	#
      b        label26                                                                                                                               	#

label25:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label26:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label27                                                                                                                   	#
      b        label28                                                                                                                               	#

label27:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label28:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (super)]
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label29                                                                                                                   	#
      b        label30                                                                                                                               	#

label29:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label30:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      la       $t0, Foo_template                                                                                                                     	# Set $t0 to point to parent vft
      lw       $t0, 8($t0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label31                                                                                                                   	#
      b        label32                                                                                                                               	#

label31:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label32:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_1                                                                                                                    	# Load constant String "\n   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label33                                                                                                                   	#
      b        label34                                                                                                                               	#

label33:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label34:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Assign 4($fp) = $v0

label35:                                                                                                                                            	# for_statement_begin
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 10                                                                                                                               	# Load constant integer 10 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label36                                                                                                                   	# go to body end if for conditional check fails
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label37                                                                                                                   	#
      b        label38                                                                                                                               	#

label37:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label38:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression
      b        label35                                                                                                                               	# jump back to beginning of for loop

label36:                                                                                                                                            	# for_statement_body_end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_1                                                                                                                    	# Load constant String "\n   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label39                                                                                                                   	#
      b        label40                                                                                                                               	#

label39:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label40:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_initialization
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Assign 4($fp) = $v0

label41:                                                                                                                                            	# for_statement_begin (unrolled)
      # for_body entered
      #     Declaration statement begin
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Store the variable "j"
      #     Declaration statement end
      # for_initialization
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0

label43:                                                                                                                                            	# for_statement_begin (unrolled)
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label45                                                                                                                   	#
      b        label46                                                                                                                               	#

label45:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label46:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label47                                                                                                                   	#
      b        label48                                                                                                                               	#

label47:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label48:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression

label44:                                                                                                                                            	# for_statement_body_end (unrolled)
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression
      # for_body entered
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0
      # for_initialization
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0

label49:                                                                                                                                            	# for_statement_begin (unrolled)
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label51                                                                                                                   	#
      b        label52                                                                                                                               	#

label51:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label52:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label53                                                                                                                   	#
      b        label54                                                                                                                               	#

label53:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label54:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression

label50:                                                                                                                                            	# for_statement_body_end (unrolled)
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression
      # for_body entered
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0
      # for_initialization
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0

label55:                                                                                                                                            	# for_statement_begin (unrolled)
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label57                                                                                                                   	#
      b        label58                                                                                                                               	#

label57:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label58:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label59                                                                                                                   	#
      b        label60                                                                                                                               	#

label59:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label60:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression

label56:                                                                                                                                            	# for_statement_body_end (unrolled)
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression
      # for_body entered
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0
      # for_initialization
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 12($fp)                                                                                                                          	# Assign 12($fp) = $v0

label61:                                                                                                                                            	# for_statement_begin (unrolled)
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label63                                                                                                                   	#
      b        label64                                                                                                                               	#

label63:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label64:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression
      # for_body entered
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label65                                                                                                                   	#
      b        label66                                                                                                                               	#

label65:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label66:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 12($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 12($fp)                                                                                                                          	# Store the incremented result
      #     End unary expression

label62:                                                                                                                                            	# for_statement_body_end (unrolled)
      # for_update entered
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression

label42:                                                                                                                                            	# for_statement_body_end (unrolled)
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_1                                                                                                                    	# Load constant String "\n   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label67                                                                                                                   	#
      b        label68                                                                                                                               	#

label67:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label68:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      li       $v0, 3                                                                                                                                	# Load constant integer 3 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Assign 4($fp) = $v0
      # while begin
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      sgt      $v0, $v0, $v1                                                                                                                         	# v0 > v1
      beq      $v0, $zero, label70                                                                                                                   	# skip to end if predicate evaluated to false

label69:                                                                                                                                            	# while_begin
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label71                                                                                                                   	#
      b        label72                                                                                                                               	#

label71:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label72:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      sub      $t0, $v0, 1                                                                                                                           	# Decrement contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      move     $v0, $t0                                                                                                                              	# Store modified value back in v0
      #     End unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      sgt      $v0, $v0, $v1                                                                                                                         	# v0 > v1
      beq      $v0, $zero, label69                                                                                                                   	#

label70:                                                                                                                                            	# while_end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_1                                                                                                                    	# Load constant String "\n   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label73                                                                                                                   	#
      b        label74                                                                                                                               	#

label73:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label74:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Assign 4($fp) = $v0
      # while begin
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 4                                                                                                                                	# Load constant integer 4 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label76                                                                                                                   	# skip to end if predicate evaluated to false

label75:                                                                                                                                            	# while_begin
      #     Declaration statement begin
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 16($fp)                                                                                                                          	# Store the variable "j"
      #     Declaration statement end
      # while begin
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 16($fp)                                                                                                                          	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 4                                                                                                                                	# Load constant integer 4 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label78                                                                                                                   	# skip to end if predicate evaluated to false

label77:                                                                                                                                            	# while_begin
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 16($fp)                                                                                                                          	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label79                                                                                                                   	#
      b        label80                                                                                                                               	#

label79:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label80:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 16($fp)                                                                                                                          	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 16($fp)                                                                                                                          	# Store the incremented result
      move     $v0, $t0                                                                                                                              	# Store modified value back in v0
      #     End unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (j)]
      # Load value of "j" to v0
      lw       $v0, 16($fp)                                                                                                                          	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 4                                                                                                                                	# Load constant integer 4 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label77                                                                                                                   	#

label78:                                                                                                                                            	# while_end
      #     Begin unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      add      $t0, $v0, 1                                                                                                                           	# Increment contents of $v0
      sw       $t0, 4($fp)                                                                                                                           	# Store the incremented result
      #     End unary expression
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 4                                                                                                                                	# Load constant integer 4 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label75                                                                                                                   	#

label76:                                                                                                                                            	# while_end
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0
      beq      $v0, $zero, label82                                                                                                                   	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_2                                                                                                                    	# Load constant String "   this is expected\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label83                                                                                                                   	#
      b        label84                                                                                                                               	#

label83:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label84:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label82:                                                                                                                                            	# else_body

label81:                                                                                                                                            	# if_statement_body_end
      li       $v0, 0                                                                                                                                	# Load constant boolean false to $v0
      beq      $v0, $zero, label86                                                                                                                   	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label87                                                                                                                   	#
      b        label88                                                                                                                               	#

label87:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label88:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      b        label85                                                                                                                               	# skip else and go to if_statement_body_end

label86:                                                                                                                                            	# else_body
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0
      beq      $v0, $zero, label90                                                                                                                   	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_4                                                                                                                    	# Load constant String "   this is also expected\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label91                                                                                                                   	#
      b        label92                                                                                                                               	#

label91:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label92:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label90:                                                                                                                                            	# else_body

label89:                                                                                                                                            	# if_statement_body_end

label85:                                                                                                                                            	# if_statement_body_end
      li       $v0, 0                                                                                                                                	# Load constant boolean false to $v0
      beq      $v0, $zero, label94                                                                                                                   	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label95                                                                                                                   	#
      b        label96                                                                                                                               	#

label95:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label96:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      b        label93                                                                                                                               	# skip else and go to if_statement_body_end

label94:                                                                                                                                            	# else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_5                                                                                                                    	# Load constant String "   honestly, this is even more expected\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label97                                                                                                                   	#
      b        label98                                                                                                                               	#

label97:                                                                                                                                            	#
      jal      _null_pointer_error                                                                                                                   	#

label98:                                                                                                                                            	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label93:                                                                                                                                            	# if_statement_body_end
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0
      beq      $v0, $zero, label100                                                                                                                  	# jump over if body if false and go to else_body
      li       $v0, 0                                                                                                                                	# Load constant boolean false to $v0
      beq      $v0, $zero, label102                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label103                                                                                                                  	#
      b        label104                                                                                                                              	#

label103:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label104:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      b        label101                                                                                                                              	# skip else and go to if_statement_body_end

label102:                                                                                                                                           	# else_body
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0
      beq      $v0, $zero, label106                                                                                                                  	# jump over if body if false and go to else_body
      b        label105                                                                                                                              	# skip else and go to if_statement_body_end

label106:                                                                                                                                           	# else_body
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0
      beq      $v0, $zero, label108                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label109                                                                                                                  	#
      b        label110                                                                                                                              	#

label109:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label110:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      b        label107                                                                                                                              	# skip else and go to if_statement_body_end

label108:                                                                                                                                           	# else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label111                                                                                                                  	#
      b        label112                                                                                                                              	#

label111:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label112:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label107:                                                                                                                                           	# if_statement_body_end

label105:                                                                                                                                           	# if_statement_body_end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_6                                                                                                                    	# Load constant String "   It is a far, far better thing that I do, than I have ever done; it is a far, far better rest that I go to than I have ever known.\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label113                                                                                                                  	#
      b        label114                                                                                                                              	#

label113:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label114:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label101:                                                                                                                                           	# if_statement_body_end
      b        label99                                                                                                                               	# skip else and go to if_statement_body_end

label100:                                                                                                                                           	# else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_3                                                                                                                    	# Load constant String "this is not the print statement you are looking for\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label115                                                                                                                  	#
      b        label116                                                                                                                              	#

label115:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label116:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label99:                                                                                                                                            	# if_statement_body_end
      #     Declaration statement begin
      #     New expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $a0, Main_template                                                                                                                    	# Create new Main object
      jal      Object.clone                                                                                                                          	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      jal      Main_init                                                                                                                             	# Initialize new object
      move     $v0, $a0                                                                                                                              	# Set $v0 = $a0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     New expression end
      sw       $v0, 20($fp)                                                                                                                          	# Store the variable "m"
      #     Declaration statement end
      #     Declaration statement begin
      #     Cast begin
      #     Instanceof begin
      #     Begin VarExpr
      #     node: [VarExpr: (m)]
      # Load value of "m" to v0
      lw       $v0, 20($fp)                                                                                                                          	#
      #     End VarExpr
      lw       $t0, 0($v0)                                                                                                                           	# Load ID of expression to t0
      li       $t1, 3                                                                                                                                	# Load ID of type to t1
      li       $t2, 1                                                                                                                                	# Load number of descendants of type to t2
      add      $t2, $t1, $t2                                                                                                                         	# Load type ID + numDescendants to t2
      sge      $v0, $t0, $t1                                                                                                                         	# Test expression ID >= type ID
      beq      $v0, $zero, label117                                                                                                                  	# Break to instanceof_end if false
      sle      $v0, $t0, $t2                                                                                                                         	# Test expression ID <= (type ID + numDescendants)
      #     instanceof_end

label117:                                                                                                                                           	#
      beq      $v0, $zero, label118                                                                                                                  	# Jump to cast_error if not subclass
      b        label119                                                                                                                              	# Jump to cast_body_end

label118:                                                                                                                                           	# cast_error
      jal      _class_cast_error                                                                                                                     	#
      # cast_body_end

label119:                                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (m)]
      # Load value of "m" to v0
      lw       $v0, 20($fp)                                                                                                                          	#
      #     End VarExpr
      #     Cast end
      sw       $v0, 24($fp)                                                                                                                          	# Store the variable "g"
      #     Declaration statement end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_7                                                                                                                    	# Load constant String "   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label120                                                                                                                  	#
      b        label121                                                                                                                              	#

label120:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label121:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (g)]
      # Load value of "g" to v0
      lw       $v0, 24($fp)                                                                                                                          	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label122                                                                                                                  	#
      b        label123                                                                                                                              	#

label122:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label123:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      la       $t0, Foo_template                                                                                                                     	# Set $t0 to point to parent vft
      lw       $t0, 8($t0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label124                                                                                                                  	#
      b        label125                                                                                                                              	#

label124:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label125:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 12($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Instanceof begin
      #     Begin VarExpr
      #     node: [VarExpr: (m)]
      # Load value of "m" to v0
      lw       $v0, 20($fp)                                                                                                                          	#
      #     End VarExpr
      lw       $t0, 0($v0)                                                                                                                           	# Load ID of expression to t0
      li       $t1, 3                                                                                                                                	# Load ID of type to t1
      li       $t2, 1                                                                                                                                	# Load number of descendants of type to t2
      add      $t2, $t1, $t2                                                                                                                         	# Load type ID + numDescendants to t2
      sge      $v0, $t0, $t1                                                                                                                         	# Test expression ID >= type ID
      beq      $v0, $zero, label128                                                                                                                  	# Break to instanceof_end if false
      sle      $v0, $t0, $t2                                                                                                                         	# Test expression ID <= (type ID + numDescendants)
      #     instanceof_end

label128:                                                                                                                                           	#
      beq      $v0, $zero, label127                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_8                                                                                                                    	# Load constant String "   eat more chikun" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label129                                                                                                                  	#
      b        label130                                                                                                                              	#

label129:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label130:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label127:                                                                                                                                           	# else_body

label126:                                                                                                                                           	# if_statement_body_end
      #     Instanceof begin
      #     Begin VarExpr
      #     node: [VarExpr: (g)]
      # Load value of "g" to v0
      lw       $v0, 24($fp)                                                                                                                          	#
      #     End VarExpr
      lw       $t0, 0($v0)                                                                                                                           	# Load ID of expression to t0
      li       $t1, 4                                                                                                                                	# Load ID of type to t1
      li       $t2, 0                                                                                                                                	# Load number of descendants of type to t2
      add      $t2, $t1, $t2                                                                                                                         	# Load type ID + numDescendants to t2
      sge      $v0, $t0, $t1                                                                                                                         	# Test expression ID >= type ID
      beq      $v0, $zero, label133                                                                                                                  	# Break to instanceof_end if false
      sle      $v0, $t0, $t2                                                                                                                         	# Test expression ID <= (type ID + numDescendants)
      #     instanceof_end

label133:                                                                                                                                           	#
      beq      $v0, $zero, label132                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_9                                                                                                                    	# Load constant String "   eat more cows" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label134                                                                                                                  	#
      b        label135                                                                                                                              	#

label134:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label135:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label132:                                                                                                                                           	# else_body

label131:                                                                                                                                           	# if_statement_body_end
      li       $v0, 0                                                                                                                                	# Load constant integer 0 to $v0
      sw       $v0, 4($fp)                                                                                                                           	# Assign 4($fp) = $v0
      # while begin
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 1000                                                                                                                             	# Load constant integer 1000 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label137                                                                                                                  	# skip to end if predicate evaluated to false

label136:                                                                                                                                           	# while_begin
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_10                                                                                                                   	# Load constant String "   it was the best of times.\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label138                                                                                                                  	#
      b        label139                                                                                                                              	#

label138:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label139:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      b        label137                                                                                                                              	# Jump to loop body end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_11                                                                                                                   	# Load constant String "   if this executes, then Windows > Linux\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label140                                                                                                                  	#
      b        label141                                                                                                                              	#

label140:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label141:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Begin VarExpr
      #     node: [VarExpr: (i)]
      # Load value of "i" to v0
      lw       $v0, 4($fp)                                                                                                                           	#
      #     End VarExpr
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 1000                                                                                                                             	# Load constant integer 1000 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      slt      $v0, $v0, $v1                                                                                                                         	# v0 < v1
      beq      $v0, $zero, label136                                                                                                                  	#

label137:                                                                                                                                           	# while_end
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_7                                                                                                                    	# Load constant String "   " to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label142                                                                                                                  	#
      b        label143                                                                                                                              	#

label142:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label143:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end
      #     Declaration statement begin
      li       $v0, 1                                                                                                                                	# Load constant integer 1 to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 2                                                                                                                                	# Load constant integer 2 to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 3                                                                                                                                	# Load constant integer 3 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      beq      $v1, $zero, label146                                                                                                                  	# Jump to divide_by_zero if $v1 is 0
      div      $v0, $v0, $v1                                                                                                                         	# v0 / v1
      b        label147                                                                                                                              	#

label146:                                                                                                                                           	# divide_by_zero
      jal      _divide_zero_error                                                                                                                    	#

label147:                                                                                                                                           	# divide_end
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $v0, $v0, $v1                                                                                                                         	# v0 + v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 10                                                                                                                               	# Load constant integer 10 to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 11                                                                                                                               	# Load constant integer 11 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      mul      $v0, $v0, $v1                                                                                                                         	# v0 * v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 2                                                                                                                                	# Load constant integer 2 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      rem      $v0, $v0, $v1                                                                                                                         	# v0 % v1
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      sub      $v0, $v0, $v1                                                                                                                         	# v0 - v1
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      li       $v0, 10                                                                                                                               	# Load constant integer 10 to $v0
      move     $v1, $v0                                                                                                                              	# Set $v1 = $v0
      # pop into $v0
      lw       $v0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      sgt      $v0, $v0, $v1                                                                                                                         	# v0 > v1
      beq      $v0, $zero, label145                                                                                                                  	# short circuit A if false, jumping over B to ||_body_end
      li       $v0, 1                                                                                                                                	# Load constant boolean true to $v0

label145:                                                                                                                                           	# ||_body_end
      bne      $v0, $zero, label144                                                                                                                  	# short circuit A if true, jumping over B to &&_body_end
      li       $v0, 0                                                                                                                                	# Load constant boolean false to $v0

label144:                                                                                                                                           	#
      sw       $v0, 28($fp)                                                                                                                          	# Store the variable "z"
      #     Declaration statement end
      #     Begin VarExpr
      #     node: [VarExpr: (z)]
      # Load value of "z" to v0
      lw       $v0, 28($fp)                                                                                                                          	#
      #     End VarExpr
      xor      $v0, $v0, 1                                                                                                                           	# Apply not operator to contents of $v0
      beq      $v0, $zero, label149                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_12                                                                                                                   	# Load constant String "Some people say that there is nothing better to do than code functionally. With a lisp I said \"I agree\"\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label150                                                                                                                  	#
      b        label151                                                                                                                              	#

label150:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label151:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label149:                                                                                                                                           	# else_body

label148:                                                                                                                                           	# if_statement_body_end
      li       $v0, 0                                                                                                                                	# Load constant boolean false to $v0
      xor      $v0, $v0, 1                                                                                                                           	# Apply not operator to contents of $v0
      beq      $v0, $zero, label153                                                                                                                  	# jump over if body if false and go to else_body
      #     Dispatch expression begin
      # push $a0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $a0, 0($sp)                                                                                                                           	#
      la       $v0, StringConst_13                                                                                                                   	# Load constant String "   it was not not not the worst of times.\n" to $v0
      # push $v0
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $v0, 0($sp)                                                                                                                           	#
      #     Begin VarExpr
      #     node: [VarExpr: (io)]
      # Load value of "io" to v0
      lw       $v0, 0($fp)                                                                                                                           	#
      #     End VarExpr
      #             Jump to null pointer error if reference is null
      beq      $v0, $zero, label154                                                                                                                  	#
      b        label155                                                                                                                              	#

label154:                                                                                                                                           	#
      jal      _null_pointer_error                                                                                                                   	#

label155:                                                                                                                                           	#
      move     $a0, $v0                                                                                                                              	# Set $a0 = $v0
      lw       $t0, 8($a0)                                                                                                                           	# Load address of vft to $t0
      lw       $t0, 16($t0)                                                                                                                          	# Load address of desired method to $t0
      jalr     $t0                                                                                                                                   	# jump to address in $t0
      # pop into $a0
      lw       $a0, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      #     Dispatch expression end

label153:                                                                                                                                           	# else_body

label152:                                                                                                                                           	# if_statement_body_end
      b        label10                                                                                                                               	# Jump to method epilog
      # 
      #     Epilog Begin

label10:                                                                                                                                            	#
      add      $sp, $fp, 32                                                                                                                          	# Remove space for 8 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $sp, $sp, 0                                                                                                                           	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 

Foo.foo:                                                                                                                                            	#
      # 
      #     Prolog Begin
      # push $ra
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $ra, 0($sp)                                                                                                                           	#
      # push $fp
      add      $sp, $sp, -4                                                                                                                          	#
      sw       $fp, 0($sp)                                                                                                                           	#
      add      $fp, $sp, 0                                                                                                                           	# Make space for 0 local vars
      move     $sp, $fp                                                                                                                              	# Initialize stack pointer
      #     Prolog End
      # 
      li       $v0, 3                                                                                                                                	# Load constant integer 3 to $v0
      b        label156                                                                                                                              	# Jump to method epilog
      # 
      #     Epilog Begin

label156:                                                                                                                                           	#
      add      $sp, $fp, 0                                                                                                                           	# Remove space for 0 local vars
      # pop into $fp
      lw       $fp, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      # pop into $ra
      lw       $ra, 0($sp)                                                                                                                           	#
      sub      $sp, $sp, -4                                                                                                                          	#
      add      $sp, $sp, 0                                                                                                                           	#
      jr       $ra                                                                                                                                   	# Return to caller
      #     Epilog End
      # 
