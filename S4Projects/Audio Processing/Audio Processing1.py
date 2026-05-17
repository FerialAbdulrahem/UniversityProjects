import numpy as np
import matplotlib.pyplot as plt
import sounddevice as sd
from scipy.fftpack import fft
from scipy.signal import find_peaks

𝑡 = np.linspace(0,3,3*44100)

F_array = np.array([130.81,146.83,164.81,174.61, 196.00, 220.00, 246.94])
f_array= np.array([261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88])

#t_array =np.array([0, 0.5, 1.0, 1.5, 2.0])
#T_array = np.array([0.5,0.5,0.5,0.5,0.5])
 
t_array = np.array([0, 1.2, 1.4, 1.6, 2.5, 3.7, 3.9, 4.1])
T_array = np.array([0.3, 0.1, 0.1, 0.1, 0.3, 0.1, 0.1, 0.1])

N=5
counter=0
x=0

while (counter<N):
    Fi = F_array[counter]
    fi = f_array[counter]
    ti = t_array[counter]
    Ti = T_array[counter]
    x = x + (np.sin(2*np.pi*Fi*t)+np.sin(2*np.pi*fi*t))*((t>=ti)&(t<=(ti+Ti)))
    counter = counter+1

# plt.plot(t, x) 
sd.play(x,3*44100)

# NOISE CANCELLATION   
 
N = 3*44100
f = np. linspace(0 , 44100/2 , int(N/2))


x_f = fft(x)
x_f = 2/N * np.abs(x_f[0:int(N/2)])



fn1 , fn2 = np.random.randint(0, 512, 2)
n = np.sin(2*np.pi*fn1*t)+np.sin(2*np.pi*fn2*t)


xn = x+n
xn_f = 2/N * np.abs(fft(xn)[:N//2])

peaks, _ = find_peaks(xn_f, height=np.max(xn_f)*0.3)
peak_freqs = f[peaks]


fft_xn= fft(xn)

# remove the peak frequencies
for peak in peaks:
    fft_xn[peak] = 0
    # Mirror index for the negative frequencies
    if peak != 0:
        fft_xn[-peak] = 0

# Inverse FFT to get the filtered signal back
x_filtered = np.real(np.fft.ifft(fft_xn))

xFiltered_f = fft(x_filtered)
xFiltered_f = 2/N * np.abs(xFiltered_f [0:int(N/2)])

sd.play(x_filtered, 3 * 44100)

#TIME DOMAIN
plt.figure()
plt.subplot(3,1,1)
plt.plot(t,x)
plt.subplot(3,1,2)
plt.plot(t,xn)
plt.subplot(3,1,3)
plt.plot(t,x_filtered)

#FREQUENCY DOMAIN
plt.figure()
plt.subplot(3,1,1)
plt.plot(f,x_f)
plt.subplot(3,1,2)
plt.plot(f,xn_f)
plt.subplot(3,1,3)
plt.plot(f,xFiltered_f)

