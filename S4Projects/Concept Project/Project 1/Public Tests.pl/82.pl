:- consult('publicKB').

:- set_prolog_flag(answer_write_options, [max_depth(0)]).

university_schedule(S) :-
    findall(Student, studies(Student, _), AllStudents),
    sort(AllStudents, UniqueStudents),
    helper(UniqueStudents,S).


helper([],[]).
helper([H|T],[H1|T2]):-
	setof(Slots, student_schedule(H,Slots),All),
	H1 = sched(H,X), 
	member(X,All) ,
	helper(T,T2).

student_schedule(Student_id, Slots) :-
    setof(Course, studies(Student_id, Course), Courses),
    findall(slot(Day, SlotNum, Course),
            (member(Course, Courses),
             day_schedule(Day, DaySlots),
             nth1(SlotNum, DaySlots, SlotCourses),
             member(Course, SlotCourses)),
            PossibleSlots),
    generate_valid_schedule(Courses, PossibleSlots, Slots),
    no_clashes(Slots),
    study_days(Slots, 5).

generate_valid_schedule([], _, []).
generate_valid_schedule([Course|Courses], PossibleSlots, [Slot|Rest]) :-
    member(Slot, PossibleSlots),
    Slot = slot(_, _, Course),
    generate_valid_schedule(Courses, PossibleSlots, Rest).

	
no_clashes([]).
no_clashes([slot(Day, SlotNum, _)|R]) :-
    \+ member(slot(Day, SlotNum, _), R),
    no_clashes(R).

	
study_days(Slots, DayCount):-
    findall(X, ( member(slot(X,_,_), Slots) ), Days),
    sort(Days, L),
    length(L, Count),
    Count =< DayCount.
	
	
is_occupied(slot(Day, SlotNumber, _), OccupiedSlots) :- 
    member(slot(Day, SlotNumber, _), OccupiedSlots).

occupied_slots(Schedules, AllOccupied) :-
    findall(slot(Day, SlotNumber, Course),(member(sched(_, Slots), Schedules), member(slot(Day, SlotNumber, Course), Slots)), AllOccupied).

all_slots(AllSlots) :-
    findall(slot(Day, SlotNumber, Course),(day_schedule(Day, Schedule), nth1(SlotNumber, Schedule, Course),nonvar(Course)), AllSlots).

study_days_list(Schedules, StudyDays) :-
    findall(Day,(member(sched(_, Slots), Schedules), member(slot(Day, _, _), Slots)),Days),sort(Days, StudyDays).  

student_study_days(StudentSchedule, StudyDays) :-
    findall(Day,(member(slot(Day, _, _), StudentSchedule)),Days),sort(Days, StudyDays).

student_days_off(StudentSchedule, DaysOff) :-
    student_study_days(StudentSchedule, StudyDays),weekday(AllDays),subtract(AllDays, StudyDays, DaysOff).

assembly_hours(Schedules, AH) :-
    occupied_slots(Schedules, O),all_slots(AllSlots),findall(slot(Day, SlotNum),(member(slot(Day, SlotNum, _), AllSlots),\+ is_occupied(slot(Day, SlotNum, _), O),forall(member(sched(_, Slots), Schedules), (student_days_off(Slots, DaysOff),\+ member(Day, DaysOff)))), AH).

weekday([saturday, sunday, monday, tuesday, wednesday, thursday]).

	




