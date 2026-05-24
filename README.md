# 🎓 Semester 4 Projects – German University in Cairo

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen)]()
[![University](https://img.shields.io/badge/University-GUC-blue)]()
[![Semester](https://img.shields.io/badge/Semester-4-orange)]()
[![Language](https://img.shields.io/badge/Languages-C,C++,Python-red)]()
[![Tools](https://img.shields.io/badge/Tools-Logisim,PSpice,Oscilloscope-lightgrey)]()

This repository showcases all my **Semester 4 projects** across Circuits, Computer Organization, Signals & Systems, and Programming Labs.  
Each project is documented with **reports, code, simulations, and results**.

---

## 🔌 Electric Circuits II Lab Project
**Course:** ELCT 401  
**Goal:** Design, simulate, and implement **band-pass** and **band-stop filters**.  

| Component | What I Did |
|-----------|------------|
| 📐 Design | Calculated resistor & capacitor values for chosen cutoff frequencies. |
| 💻 Simulation | Ran **PSpice AC sweep & time domain** simulations to verify gain. |
| 🔧 Hardware | Built the circuit on a breadboard using LM741 op-amps. |
| 📊 Results | Captured oscilloscope readings at different frequencies. |
| 📝 Report | Documented design, simulation, hardware, and evaluation. |

---

## 💻 Computer Organization – Milestone 1
**Course:** CSEN/CSIS 402  
**Goal:** Build a **basic computer subset** in Logisim.  

| Component | What I Did |
|-----------|------------|
| 🗂️ Memory | Implemented 128-word RAM with 16-bit words. |
| 🔢 Registers | Built AR, DR, TR, AC, PC, IR with load/clear/inc controls. |
| ➕ ALU | Implemented operations: transfer, add, AND, complement, multiply. |
| 🔄 RTL | Wrote micro-operations to compute `M[4] = M[0] * (-M[3])`. |
| ✅ Testing | Verified cycle-by-cycle execution with control signals. |

---

## ⚙️ Computer Organization – Milestone 2
**Course:** CSEN/CSIS 402  
**Goal:** Extend Milestone 1 with a **control unit**.  

| Instruction | What I Did |
|-------------|------------|
| LDA | Load accumulator from memory. |
| MUL | Implemented multiplication using unused opcode. |
| ADD | Added memory values to accumulator. |
| STA | Stored accumulator back to memory. |
| ISZ | Incremented and skipped if zero. |
| BUN | Branch unconditional. |
| AND | Logical AND with memory. |

📊 I created timing diagrams for each instruction and tested with different values of A, B, and C.  
**Outcome:** A working Logisim computer capable of running a small assembly program.

---

## 🎶 Signals & Systems – Audio Processing
**Course:** COMM 401  
**Goal:** Generate piano signals, add noise, and remove it using FFT.  

| Step | What I Did |
|------|------------|
| 🎹 Signal Generation | Created piano note signals from 3rd & 4th octaves. |
| 🎵 Song | Combined notes into a melody using Python. |
| 📈 Noise | Added random sinusoidal noise components. |
| 🔬 Filtering | Applied FFT to detect and remove noise peaks. |
| 📊 Visualization | Produced time-domain & frequency-domain plots. |

**Outcome:** A clean audio signal restored after noise cancellation.

---

## 🎲 Jackaroo Game – Programming Lab
**Course:** Programming Lab  
**Goal:** Reimagine Jackaroo as a **single-player game vs 3 CPUs**.  

| Feature | What I Did |
|---------|------------|
| 🃏 Deck | Designed 102-card deck with Ace, King, Jack, Queen, Wild cards. |
| 🔵 Marbles | Implemented movement, swapping, burning, saving rules. |
| ⚔️ CPU Logic | Programmed CPU opponents to play cards randomly. |
| 🎯 Strategy | Added trap cells, safe zones, and firepit mechanics. |

**Outcome:** A fully playable Jackaroo adaptation with custom rules and strategic gameplay.

---

## 🛠️ Tech Stack
- **Logisim** – Digital circuits  
- **PSpice** – Analog simulation  
- **Python** – Signal processing  
- **C / C++** – Programming lab implementations  
- **Oscilloscope & Breadboard** – Hardware testing  

---

