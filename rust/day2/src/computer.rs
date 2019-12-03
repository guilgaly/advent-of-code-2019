pub type Address = usize;
pub type Memory = Vec<usize>;

pub fn execute_program(program: &Memory, noun: usize, verb: usize) -> Result<Memory, String> {
    let mut memory = program.clone();
    memory[1] = noun;
    memory[2] = verb;

    let mut current_address: usize = 0;
    loop {
        let instruction = parse_instruction(&memory, current_address)?;
        match instruction {
            Instruction::Addition(params) => {
                let instr_res = memory[params.left] + memory[params.right];
                memory[params.output] = instr_res;
                current_address += 4;
            }
            Instruction::Multiplication(params) => {
                let instr_res = memory[params.left] * memory[params.right];
                memory[params.output] = instr_res;
                current_address += 4;
            }
            Instruction::Halt => {
                break;
            }
        }
    }
    Ok(memory)
}

fn parse_instruction(memory: &Memory, address: Address) -> Result<Instruction, String> {
    fn find_at<'a>(memory: &'a Memory, index: Address, name: &str) -> Result<&'a usize, String> {
        memory.get(index).ok_or(format!("{} out of range at index {}", name, index))
    }

    fn parse_params(memory: &Memory, address: Address) -> Result<(Address, Address, Address), String> {
        let left_addr = find_at(memory, address + 1, "left operand reference")?;
        let right_addr = find_at(memory, address + 2, "right operand reference")?;
        let output_addr = find_at(memory, address + 3, "output position reference")?;

        let _ = find_at(memory, *left_addr, "left operand")?;
        let _ = find_at(memory, *right_addr, "right operand")?;
        let _ = find_at(memory, *output_addr, "output position")?;

        Ok((*left_addr, *right_addr, *output_addr))
    }

    let code = find_at(memory, address, "opcode position")?;
    let opcode = Opcode::from_code(*code)?;
    match opcode {
        Opcode::Add => {
            let (left, right, output) = parse_params(memory, address)?;
            Ok(Instruction::addition(left, right, output))
        }
        Opcode::Mult => {
            let (left, right, output) = parse_params(memory, address)?;
            Ok(Instruction::multiplication(left, right, output))
        }
        Opcode::End => Ok(Instruction::Halt),
    }
}

enum Opcode {
    Add,
    Mult,
    End,
}

impl Opcode {
    pub fn from_code(i: usize) -> Result<Opcode, String> {
        match i {
            1 => Ok(Opcode::Add),
            2 => Ok(Opcode::Mult),
            99 => Ok(Opcode::End),
            _ => Err(format!("Illegal opcode {}", i))
        }
    }
}

#[derive(Debug)]
#[derive(PartialEq)]
enum Instruction {
    Addition(Box<InstructionParams>),
    Multiplication(Box<InstructionParams>),
    Halt,
}

impl Instruction {
    pub fn addition(left: Address, right: Address, output: Address) -> Instruction {
        let x = Instruction::boxed_params(left, right, output);
        Instruction::Addition(x)
    }

    pub fn multiplication(left: Address, right: Address, output: Address) -> Instruction {
        Instruction::Multiplication(Instruction::boxed_params(left, right, output))
    }

    fn boxed_params(left: Address, right: Address, output: Address) -> Box<InstructionParams> {
        Box::new(InstructionParams { left, right, output })
    }
}

#[derive(Debug)]
#[derive(PartialEq)]
struct InstructionParams {
    left: Address,
    right: Address,
    output: Address,
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn parse_instruction_test() {
        let prog1 = vec![1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert_eq!(parse_instruction(&prog1, 0).unwrap(), Instruction::addition(9, 10, 3));
        assert_eq!(parse_instruction(&prog1, 4).unwrap(), Instruction::multiplication(3, 11, 0));
        assert_eq!(parse_instruction(&prog1, 8).unwrap(), Instruction::Halt);

        let prog2 = vec![0, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog2, 0).is_err());

        let prog3 = vec![100, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog3, 0).is_err());

        let prog4 = vec![1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog4, 12).is_err());

        let prog5 = vec![1, 9, 10];
        assert!(parse_instruction(&prog5, 0).is_err());

        let prog6 = vec![1, 9, 10, 12, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog6, 0).is_err());
    }

    #[test]
    fn execute_program_test() {
        assert_eq!(execute_program(&vec![1, 0, 0, 0, 99], 0, 0).unwrap(), vec![2, 0, 0, 0, 99]);
        assert_eq!(execute_program(&vec![2, 3, 0, 3, 99], 3, 0).unwrap(), vec![2, 3, 0, 6, 99]);
        assert_eq!(execute_program(&vec![2, 4, 4, 5, 99, 0], 4, 4).unwrap(), vec![2, 4, 4, 5, 99, 9801]);
        assert_eq!(execute_program(&vec![1, 1, 1, 4, 99, 5, 6, 0, 99], 1, 1).unwrap(), vec![30, 1, 1, 4, 2, 5, 6, 0, 99]);
    }
}