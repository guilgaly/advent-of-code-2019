use computer::Memory;

mod computer;

fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);
    let program: Memory =
        input_str
            .split(',')
            .filter_map(|line| line.parse::<i32>().ok())
            .collect();

    match computer::execute_program(&program) {
        Err(err) =>
            println!("[part 1] Program crashed with error message: {}", err),
        Ok(res) => {
            println!("[part 1] Program completed successfully with end state: {:?}", res)
        }
    }
}
