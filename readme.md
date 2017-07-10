# Lorenzo il Magnifico

## Building
Issue ```mvn package``` in the root directory

## Running
Use the `start-client.sh` and `start-server.sh` commands

## Requirements
The CLI was developed and tested on Linux.
It needs at least 140 columns to display properly.

It should work on any terminal supported by JLine2,
and fallback to a default size if JLine is unable to get the size of the terminal.

Because of a limitation of the library resizing of the terminal window is NOT supported.
This limitation is not easy to overcome without using JNI or dirty, non portable hacks.

## How to use the client
At any moment the `help` command is available to get help.

Be aware of the main commands available when making a choice (the most used functionality of the CLI):
- show: show choosable options
- choose <n>: choose
- done: confirm the choice (in case multiple choices are allowed)
- back: go back