library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity DigitalClock_tb is
end DigitalClock_tb;

architecture testbench of DigitalClock_tb is
signal clk : std_logic := '0';
signal reset : std_logic := '1';
signal hours : integer range 0 to 23;
signal minutes : integer range 0 to 59;
signal seconds : integer range 0 to 59;
constant clk_period : time := 20 ps; -- 50 GHz clock period
component DigitalClock
port (
clk : in std_logic;
reset : in std_logic;
hours : out integer range 0 to 23;
minutes : out integer range 0 to 59;
seconds : out integer range 0 to 59
);
end component;
begin
uut: DigitalClock port map (
clk => clk,
reset => reset,
hours => hours,
minutes => minutes,
seconds => seconds
);
-- Clock generation
clk_process: process
begin
clk <= '0';
wait for clk_period / 2;
clk <= '1';
wait for clk_period / 2;
end process;
-- Test process: Apply reset at least once, then continue
stimulus: process
begin
    reset <= '1';  -- Assert reset active low
    wait for 100 ns;  -- Hold reset to initialize counters
    reset <= '0';  -- Release reset
    --hanzwed statement to wait for rising edge an let the reset=1
wait until rising_edge(clk); -- Wait for the specific clock edge
reset <= '1';                -- Release the reset (Active Low: 1 = Run, 0 = Reset)
    wait for 1_000_000_000 ns;  -- Simulate for 1 second (to see seconds increment)
    
    wait;  -- Stop simulation here
end process;
end testbench;

