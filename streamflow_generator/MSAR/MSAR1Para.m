function [A B] = MSAR1Para( Z )
%MSAR1 MSAR1 model
%  Z is a 3-d standalized matrix  which is all data
[n l m]=size(Z);
A=zeros(m,m,l);
B=zeros(m,m,l);% l seasons
for i=1:l
    M1tau=Mktau( Z ,1, i);
    M0tau_1=Mktau(Z ,0 ,i-1);
    A1tau=M1tau/M0tau_1;
    A(:,:,i)=A1tau;
    M0tau=Mktau(Z ,0 ,i);
    BB=M0tau-A1tau*M1tau';
    Btau = chol(BB,'lower') ;%cholesky decomposition for B
    B(:,:,i)=Btau;
end
end


