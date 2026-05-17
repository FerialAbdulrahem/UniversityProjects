library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity DigitalClock is
    port (
        clk     : in std_logic;
        reset   : in std_logic; 
        hours   : out integer range 0 to 23;
        minutes : out integer range 0 to 59;
        seconds : out integer range 0 to 59
    );
end DigitalClock;

architecture Behavioral of DigitalClock is
    -- Removed the huge "sec_counter" because Instructions say 1 cycle = 1 second.
    signal sec : integer range 0 to 59 := 0;
    signal min : integer range 0 to 59 := 0;
    signal hr  : integer range 0 to 23 := 0;
begin
    process (clk)
    begin
        if rising_edge(clk) then
            if reset = '0' then -- Synchronous active-low reset 
                sec <= 0;
                min <= 0;
                hr  <= 0;
            else
                -- Every clock cycle is 1 second 
                if sec = 59 then
                    sec <= 0;
                    if min = 59 then
                        min <= 0;
                        if hr = 23 then
                            hr <= 0;
                        else
                            hr <= hr + 1;
                        end if;
                    else
                        min <= min + 1;
                    end if;
                else
                    sec <= sec + 1;
                end if;
            end if;
        end if;
    end process;

    -- Connect internal signals to outputs
    hours   <= hr;
    minutes <= min;
    seconds <= sec;

end Behavioral;

