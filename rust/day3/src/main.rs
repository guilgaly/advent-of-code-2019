use wiring::Wire;

mod wiring;

fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);

    match execute(&input_str) {
        Err(err) => println!("Error: {}", err),
        Ok(()) => (),
    }

    println!("Hello, world!");
}

fn execute(input_str: &str) -> Result<(), String> {
    let (w1, w2) = parse_input(&input_str)?;

    let shortest_distance =
        wiring::find_shortest_distance(&w1, &w2)
            .ok_or("No intersection founc")?;
    println!("[part 1] Shortest distance: {}", shortest_distance);

    let shortest_path =
        wiring::find_shortest_path(&w1, &w2)
            .ok_or("No intersection founc")?;
    println!("[part 2] Shortest path: {}", shortest_path);

    Ok(())
}

fn parse_input(str: &str) -> Result<(Wire, Wire), String> {
    let maybe_wires: Result<Vec<Wire>, String> =
        str.lines()
            .take(2)
            .map(|s| wiring::parse_wire(s))
            .collect();
    let mut wires = maybe_wires?;
    let w2 = wires.pop().ok_or("Missing first wire")?;
    let w1 = wires.pop().ok_or("Missing second wire")?;

    Ok((w1, w2))
}

//let wires_st: Result<Vec<Wire>, String> =
//input_str.lines()
//.take(2)
//.map(|s| wiring::parse_wire(s)).
//collect();
