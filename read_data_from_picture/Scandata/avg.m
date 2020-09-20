function vecResult = avg(picture)
%picture��һ���Ҷ�ͼ��RGB���󣬻Ҷ�ͼ��R��G��Bֵ��һ���ģ�������ֻ���ֳ�ԭ��ɫ����ת���ɻҶ�ͼ������أ�
%��������һ��m*1�����ʾ���ĸ���������ƽ��ֵ�Ľ��������ÿһ�У����Ԫ���ж���RGBֵ�Ƿ�С��150
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

            