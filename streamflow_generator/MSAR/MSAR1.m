function [ Z ] = MSAR1( A,B,epsilon,m,n,l)
%MSAR1 
%   
z=zeros(m,l,n);%n years,l seasons,m stations
for i=1:n
    epsilont=epsilon(:,:,i);
    for j=1:l;
        if j==1
           if (i==1)
               z(:,j,i)=zeros(m,1);
           else
               z(:,j,i)=A(:,:,j)*z(:,l,i-1)+B(:,:,j)*epsilont(:,j);
           end
        else
            z(:,j,i)=A(:,:,j)*z(:,j-1,i)+B(:,:,j)*epsilont(:,j);
        end      
    end
end
%after gettingthe simulation results,transform them to the original format
Z=zeros(n,l,m);
for i=1:n
    for j=1:l
        for k=1:m
            Z(i,j,k)=z(k,j,i);
        end
    end
end
end

