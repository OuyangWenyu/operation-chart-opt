function vecResult = avg(picture)
%picture是一个灰度图的RGB矩阵，灰度图的R、G、B值是一样的，本函数只区分出原红色像素转换成灰度图后的像素，
%首先设置一个m*1矩阵表示最后的各个横坐标平均值的结果。对于每一列，逐个元素判断其RGB值是否小于150
m=size(picture,2);
vecResult=zeros(m,1);
n=size(picture,1);
for i=1:m
    vec=zeros(n,1);
    count=0;
    for j=1:n
        if(picture(j,i,1)<180 && picture(j,i,1)>30)
            vec(j,1)=1;
            count=count+1;
        end;
    end;
    vecIndexTemp=[1:1:n];
    vecIndex=vecIndexTemp';
    vecResult(i)=sum(vec.*vecIndex)/count;
end;

            