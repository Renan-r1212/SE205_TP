Question 1.5:
Expliquez comment cette procédure (delay_until) peut
faire attendre la tâche bien au delà de l’échéance si l’exécution de
gettimeofday n’est pas immédiatement de l’exécution de nanosleep.

La fonction nanosleep n'est pas valables que pour les processus (elles
endorment le processus entier si on l'utilise dans un thread) et en plus
elles ne permettent pas d'obtenir un delai avec un précision inférieure à 10ms.
Par conséquent, si un thread effectue la fonction nsleep, tous les autres threads
seront bloqués au lieu du seul thread qui l'a invoqué et aussi si deadline être.