import numpy as np
print('Enter weights')
w1=input('Weight w1=')
w2=input('weight w2=')
print('Enter Threshold Value')
theta=input('theta=')
# AND function input
y = np.array([0,0,0,0])
x1 = np.array([0,0,1,1])
x2 = np.array([0,1,0,1])
z = np.array([0,0,0,1])
zin = np.array([0,0,0,0])
con=1;

while con:
  i=0;
  for i in range(4):
    x11=x1[i]
    x22=x2[i]
    zin=x11*w1+x22*w2
    if (zin>=theta):
       y[i]=1
    else:
       y[i]=0
    if (y[i]==z[i]):
      con=0
    else:
      print('Net is not learning enter another set of weights and Threshold value')
      w1=input('weight w1=')
      w2=input('weight w2=')
      theta=input('theta=')
      i=-1
    i=i+1
print('Output of Net')
print(y)
print('Mcculloch-Pitts Net for AND function')
print('Weights of Neuron')
print(w1)
print(w2)
print('Threshold value')
print(theta)