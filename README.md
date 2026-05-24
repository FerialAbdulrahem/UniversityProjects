# 🎓 Semester 4 Projects – German University in Cairo

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen)]()
[![University](https://img.shields.io/badge/University-GUC-blue)]()
[![Semester](https://img.shields.io/badge/Semester-4-orange)]()
[![Languages](https://img.shields.io/badge/Languages-C,C++,Python,Java,Logisim,PSpice-darkpink)]()
[![Tools](https://img.shields.io/badge/Tools-Logisim,PSpice,Oscilloscope,Breadboard-lightgrey)]()

This repository contains all my **Semester 4 projects**. Each project demonstrates a different skill set — from hardware circuits to digital computer design, signal processing, and game development.  

---

## 🔌 Electric Circuits II Lab Project
**Course:** ELCT 401 – Electric Circuits II  
**Languages/Tools:** PSpice, Oscilloscope, Breadboard  

### What I Did
- Designed **band-pass** and **band-stop filters** by calculating resistor and capacitor values for chosen cutoff frequencies.  
- Simulated the circuits in **PSpice** using AC sweep and time-domain analysis.  
- Built the hardware on a breadboard using LM741 op-amps, resistors, and capacitors.  
- Tested with an oscilloscope at three frequencies (f < fo, f = fo, f > fo) to confirm filter behavior.  
- Wrote a detailed report with design equations, simulation screenshots, hardware photos, and oscilloscope captures.  

**Outcome:** A fully working filter circuit that matched theoretical predictions, simulation results, and hardware implementation.

---

## 💻 Computer Organization Project
**Course:** CSEN/CSIS 402 – Computer Organization  
**Languages/Tools:** Logisim  

### What I Did
- Built a **bus-based architecture** with memory (128 words, 16-bit each), registers (AR, DR, TR, AC, PC, IR), and an ALU.  
- Implemented ALU operations: transfer, add, AND, complement, multiplication.  
- Wrote RTL micro-operations to compute `M[4] = M[0] * (-M[3])`.  
- Designed a **control unit** with decoders, counters, and logic gates to manage instruction execution.  
- Implemented instructions: `LDA, MUL, ADD, STA, ISZ, BUN, AND`.  
- Created timing diagrams for each instruction and verified cycle-by-cycle execution.  
- Debugged Logisim issues (e.g., asynchronous clears, memory persistence) and ensured stable operation.  

**Outcome:** A complete digital computer in Logisim capable of running a small assembly program with arithmetic, logic, branching, and looping.

---

## 🎶 Signals & Systems – Audio Processing
**Course:** COMM 401 – Signal and System Theory  
**Languages/Tools:** Python (NumPy, Matplotlib, SciPy, SoundDevice)  

### What I Did
- Generated piano note signals from the 3rd & 4th octaves using sine waves.  
- Combined notes into a melody to simulate a short song.  
- Added random sinusoidal noise to the signal.  
- Applied **FFT-based filtering** to detect and remove noise peaks in the frequency domain.  
- Produced time-domain and frequency-domain plots (clean, noisy, filtered).  
- Played the signals using Python’s sound libraries to confirm audio quality.  

**Outcome:** A clean audio signal restored after noise cancellation, with clear visual plots and audible improvement.

---

## 🎲 Jackaroo Game – Programming Lab
**Course:** Programming Lab  
**Languages/Tools:** Java (JavaFX for GUI), C++ (logic prototyping)  

### What I Did
- Reimagined the traditional **Jackaroo board game** into a single-player version against 3 CPU opponents.  
- Designed a **102-card deck** with Ace, King, Jack, Queen, and Wild cards, each with custom rules.  
- Implemented marble mechanics: movement, swapping, burning, saving, trap cells, safe zones, and firepit.  
- Programmed CPU logic to play cards randomly and interact strategically with the player.  
- Built a **JavaFX GUI** to visualize the board, marbles, and card actions.  
- Structured the game using **object-oriented programming** principles for clarity and scalability.  

**Outcome:** A fully playable Jackaroo adaptation with custom rules, strategic mechanics, CPU opponents, and a JavaFX-based interface.

---

## 🛠️ Tech Stack
- **Logisim** – Digital computer design  
- **PSpice** – Circuit simulation  
- **Python** – Signal generation, FFT filtering, audio playback  
- **C / C++** – Low-level programming and prototyping  
- **Java (JavaFX)** – Game development and GUI design  
- **Oscilloscope & Breadboard** – Hardware testing for circuits  

---

## 📅 Timeline
- 🗓️ Circuits Project Evaluation: May 13–19, 2026  
- 🗓️ Computer Organization Project: April–May 2025  
- 🗓️ Signals Project: Spring 2026  
- 🗓️ Jackaroo Game: Spring 2025  


