T=0.01;

Hf = ss(0.5, 0.7, 0.2, 0.3, T);

step(Hf)

H0 = c2d(tf(1, [T, 1]), T)

Hc = minreal(H0 / Hf / (1 - H0))

[num, den] = ss2tf(Hc.A, Hc.B, Hc.C, Hc.D);
Hc = minreal(tf(num, den, T))

step(feedback(Hc, Hf), feedback(Hf*Hc, 1))


