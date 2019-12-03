use computer::Memory;

mod computer;

fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);
    let program: Memory =
        input_str
            .split(',')
            .filter_map(|line| line.parse::<usize>().ok())
            .collect();

    part1(&program);

    part2(&program);
}

fn part1(program: &Memory) {
    match computer::execute_program(program, 12, 2) {
        Err(err) =>
            println!("[part 1] Program crashed with error message: {}", err),
        Ok(res) => {
            println!("[part 1] Program completed successfully with end state: {:?}", res)
        }
    }
}

fn part2(program: &Memory) {
    fn find_noun_and_verb(program: &Memory) -> Result<(usize, usize), String> {
        let mut res: Result<(usize, usize), String> = Err("Unable to find a valid noun and verb".to_string());
        'outer_loop: for noun in 0..99 {
            for verb in 0..99 {
                let iter_res = computer::execute_program(program, noun, verb)?;
                if iter_res[0] == 19690720 {
                    res = Ok((noun, verb));
                    break 'outer_loop;
                }
            }
        }
        res
    }

    match find_noun_and_verb(program) {
        Err(err) => {
            println!("[part 2] Program crashed with error message: {}", err);
        }
        Ok((noun, verb)) => {
            println!("[part 2] Found noun: {}, verb: {}", noun, verb);
            let result = 100 * noun + verb;
            println!("[part 2] Final result: {}", result);
        }
    }
}
