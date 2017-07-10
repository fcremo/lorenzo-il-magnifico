# Lorenzo il Magnifico
Project for the course Software Engineering 1 by Filippo Cremonese and Giulia Boggiali.

## Building
`mvn package` in the root directory

## Running
Use the `start-client.sh` and `start-server.sh` commands in the root directory

## Requirements and limitations
The CLI was developed and tested on Linux.
It needs at least 140 columns to display properly. The more, the better.

It should work on any terminal supported by JLine2 and fallback to a default size if
 JLine is unable to get the size of the terminal.

Because of a limitation of JLine resizing of the terminal window is NOT supported.
This limitation is not easy to overcome without using JNI or dirty, non portable hacks.

The best option would be to use a different wrapper around ncurses that allows 
low-level access to the terminal.

**Please start the CLI in a fullscreen terminal**

## How to use the client
At any moment the `help` command is available to get help.

Be aware of the main commands available when making a choice (the most used functionality of the CLI):
- `show` show choosable options
- `choose <n>` choose <nth> item
- `done` confirm the choice (in case multiple choices are allowed)
- `back` go back

## Notable features and design choices
### Contextual CLI
Both living in the terminal, we wanted to write an enjoyable command line interface.
We took inspiration from a project shared by prof. Campi and used a contextual paradigm to implement our CLI that we
think is very elegant.
Our CLI has support for command history (press up and down arrows, or CRTL+R to search like in a shell) and adding
support for command autocompletion with TAB should be very easy thanks to JLine.
Unfortunately we did't have time to write the code.

### Same game logic code shared the server and the client
Perhaps the most important thing for a game of this kind, we decided to try to share the most possible game logic code
between the server and the client.
That way it should be much more difficult for the server and the clients to get out of sync.

### Asynchronous networking calls
All our networking calls are asynchronous.
The server asks the clients to do something, remembers who was performing which action and goes on.
When the response from the clients comes back it is forwarded to the game controller that 
thoroughly validates it and then it gets forwarded back to all the clients so they can update the game state 
for themselves.

Asynchronous networking should make network error recovery and persistence easier since the server is already keeping
all the game state at all times and is never stuck waiting for a method call to return.
Unfortunately we did not have time to implement those features.

### Highly configurable game
Almost the whole game configuration is loaded from files.
That includes all aspects of the development cards, the leader cards, and board bonuses, including effects which are not
static but are dynamically configured.

## A note on commit authorship (by Filippo)
My laptop's fan broke just before the deadline.
The last commits are all by Giulia because she lent me her computer and I forgot to change the username.
Commit authorship metrics are not really that telling in our case anyway 
because we wrote most of the code in peer programming.
