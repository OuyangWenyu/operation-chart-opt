function [ M ] = Mktau( X ,k, tau)
%MKTAU calculate correlation matrix according to sample,lag=k
%   X is a 3-dimension matrix,cal season tau's M
% M is tau season correlation matrix
[n l m]=size(X);%n years l seasons m stations
Z=X;
M=zeros(m);
for i=1:m
    Z(:,:,i)=bsxfun(@minus,Z(:,:,i),mean(Z(:,:,i)));
end;
for i=1:m
    for j=1:m
        I=Z(:,:,i);
        J=Z(:,:,j);
        if k==0
            if tau==0
                x1=I(:,l);
                x2=J(:,l);
                M(i,j)=x1'*x2/((sum(x1.^2)*sum(x2.^2))^(1/2));
            else
                x1=I(:,tau);
                x2=J(:,tau);
                M(i,j)=x1'*x2/((sum(x1.^2)*sum(x2.^2))^(1/2));
            end
        else
            if tau<=k  %need a misalignment
                x1=I(2:n,tau);
                x2=J(1:n-1,tau+l-k);%left shift k  equal to right shift l-k
                M(i,j)=x1'*x2/((sum(x1.^2)*sum(x2.^2))^(1/2));
            else
                x1=I(:,tau);
                x2=J(:,tau-k);
                M(i,j)=x1'*x2/((sum(x1.^2)*sum(x2.^2))^(1/2));
            end
        end                       
    end;
end;
M;

