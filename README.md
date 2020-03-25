# Cjube

A very simple (for now) representation for 3x3 riddle cube.

## Usage

Cuurently only in REPL (with `lein repl`).

`(rotate <cube> <moves>)` will perform a series of rotations. The moves are represented as a string of [standard cube notation](https://ruwix.com/the-rubiks-cube/notation/advanced/). The initial cube is represented by `solved-cube`.

In an ANSI terminal (preferably dark-themed), print the cube with `(print-cube <cube>)`.

Example:

``` clojure
(print-cube (rotate solved-cube "F2 R U"))
```

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
