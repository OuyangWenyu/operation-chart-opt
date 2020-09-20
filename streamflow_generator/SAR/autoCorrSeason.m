function [ R ] = autoCorrSeason( X )
%calculate autocorrelations of sequences which are columns of a
%matrix£¬while lag=1
%the whole process is a seasonal stational process
%the return value is a row vector whose element means r_1_tau
[m n]=size(X);
R=zeros(1,n);
for i=1:n
  if i==1
      x1=X(1:m-1,n);
      x2=X(2:m,1);
      r=corrcoef(x1,x2);
      R(i)=r(2,1);
  else
      x1=X(:,i-1);
      x2=X(:,i);
      r=corrcoef(x1,x2);
      R(i)=r(2,1);
  end;
end
R;

