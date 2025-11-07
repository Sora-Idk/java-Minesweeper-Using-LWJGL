# java-Minesweeper-Using-LWJGL
Mini Project I made for my S.E. Computer Engineering Submission, A simple implementation of Minesweeper with bare bones graphics

Hi, Im Sora.
This one started as a simple idea — recreate Minesweeper, but strip away the comfort. No GUI toolkit, no click events, no ready-made sprites. Just OpenGL, a keyboard, and the will to make logic visible.

So I built it from scratch using LWJGL. Every square, every color, every move is drawn directly — the grid is alive because I tell it to be, not because some framework decided to.

The board itself is a 9x9 grid. Each cell knows three truths: whether it’s a mine, whether it’s been revealed, and whether it’s been flagged. That’s it. Everything else — the visuals, the feel, the tension — emerges from those three flags of state.

You move the cursor using the arrow keys. Press space to dig. Press F to flag. That’s all the control you get — minimal and immediate. The moment you hit a mine, the screen tells you — BOOM — and it’s over. No drama, no animation. Just an instant consequence.

The logic runs quite beneath it all. For every cell you open, the code counts the number of mines surrounding it. If it’s zero, it expands outward recursively — the flood reveals a chain reaction of safety spreading across the board. When every safe cell is uncovered, the console whispers back: You’ve won.

Visually, I kept it Spartan. Hidden tiles stay in dark grey. Safe tiles brighten up. Mine's flash red. Flags are little red squares you drop like warnings. The cursor glows in yellow — a small reminder of where your focus lives. I didn’t use text for numbers; instead, I used faint blue dots to show nearby danger. It feels quieter that way—more instinct than interface.

There’s no GUI layer, no event listener hiding behind — just OpenGL commands. Each tile you see is a quad drawn by hand, every line loop controlled directly. You feel every pixel because you draw every pixel.

And that’s what I wanted: control.
To touch the game at its roots.
To make something old, familiar, and logical — but without the safety net of comfort libraries.

Maybe later I’ll add numbers. Maybe a timer. Maybe a leaderboard. But for now, this version stands exactly how I wanted it to: a silent test of precision and patience — just you, the grid, and the mines that wait beneath it.

Until next time. o7
