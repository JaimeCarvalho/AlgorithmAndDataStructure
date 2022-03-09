# AlgorithmAndDataStructure

Proportional Representation

It is intended with this project the creation of a program that will allow us to compare the distribution methods of mandates in USA. Our objective is to assign 105 mandates to the states, so that the number of
mandates that each state receives is proportionate to its population. A value that represents this proportionality is the quota that each state would be entitled to, if it were possible
assign fractional seats. This value is obtained by multiplying the number of seats by the fraction of the total population that belongs to the state.

In this project the distribution of mandates throughout the several states of United States of America is considered using the following methods, that will only focus on the:

  - **Hamilton** - The Hamilton/Vinton Method sets the divisor as the proportion of the total population per house seat. After each state's population is divided by the divisor, the whole number of the quotient is kept and the fraction dropped. This will result in surplus house seats. The first surplus seat is assigned to the state with the largest fraction after the original division. The next is assigned to the state with the second-largest fraction and so on.
  - **Jefferson** - Divide each state’s population by the divisor to determine how many representatives it should have. Record this answer to several decimal places. This answer is called the quota. Cut off all the decimal parts of all the quotas. These are the lower quotas. Add up the remaining whole numbers. This answer will always be less than or equal to the total number of representatives. If the total was less than the total number of representatives, reduce the divisor and recalculate the quota and allocation. Continue doing this until the total in Step 3 is equal to the total number of representatives. The divisor we end up using is called the modified divisor or adjusted divisor.
  - **Webster** - The Webster Method is a modified version of the Hamilton/Vinton method. After the state populations are divided by the divisor, those with quotients that have fractions of 0.5 or above are awarded an extra seat. States with a quotient with a fraction below 0.5 have the fraction dropped. The size of the house of representatives is set in order to calculate the divisor, but can be increased in the final apportionment if a large number of states have fractions above 0.5.
  - **Huntington-Hill** - The Huntington-Hill Method is a modified version of the Webster method, but it uses a slightly different rounding method. While Webster's method rounds at 0.5, the Huntington-Hill method rounds at the geometric mean (the square root of the two number's product). If a state's quotient is higher than its geometric mean, it will be allocated an additional seat. 
  - **Adam** - Adams’s method divides all populations by a modified divisor and then rounds the results up to the upper quota. Just like Jefferson’s method we keep guessing modified divisors until the method assigns the correct number of seats.


Throughout this project I had the chance to showcase the use of some algorithms such as **Binary Search**, **Quick Sort**, etc
