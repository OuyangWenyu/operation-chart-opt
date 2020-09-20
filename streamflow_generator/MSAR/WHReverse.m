%W-H reverse transform while Cs<2
% Yt is a sequence that has been standalized
function Z=WHReverse(X)
Y=standalize(X);
Cs=skewnessCoef(X);
CY=bsxfun(@times, Y,Cs);
CYtemp=CY/2+1;
CZtemp=nthroot(CYtemp,3)-1;%while extract a cube root,two imaginary roots and one real root ,choose the real one
CZ=bsxfun(@rdivide,CZtemp*6,Cs);
Z=bsxfun(@plus,CZ,Cs/6);
Z;