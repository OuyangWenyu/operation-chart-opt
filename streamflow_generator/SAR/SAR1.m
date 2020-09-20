function [ z ] = SAR1( m,n, fai_1,epsilon_t_tau)
%SAR1 Summary of this function goes here
%   Detailed explanation goes here
z=zeros(m,n);
for i=1:m
    for j=1:n
       if (j==1) 
           if (i==1)
               z(i,j)=0;
           else
              z(i,j)=fai_1(j)*z(i-1,n)+epsilon_t_tau(i,j); 
           end
       else
           z(i,j)=fai_1(j)*z(i,j-1)+epsilon_t_tau(i,j);
       end
    end
end
z;

