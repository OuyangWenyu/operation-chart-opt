function Z=foreanfis(n_data,n_day)
%untuk meramal pake anfis

A=urutgabungfile(n_data,n_day);
t = A(:, 1); 
x = A(:, 2); 
plot(t, x);

[m,n]=size(A);

for t=19:m-7
Data(t-18,:)=[x(t-18) x(t-12) x(t-6) x(t) x(t+6)]; 
end
tx=floor((m-7-19)/2);
trnData=Data(1:tx, :);
size(trnData)
chkData=Data(tx+1:2*tx, :);
size(chkData)
fismat = genfis1(trnData);

[fismat1,error1,ss,fismat2,error2] = anfis(trnData,fismat,[],[],chkData);

plot([error1; error2]);

anfis_output = evalfis([trnData; chkData], fismat2);
index = 26:m;
subplot(211), plot(t(index), [x(index) anfis_output]);
subplot(212), plot(t(index), x(index) - anfis_output);

