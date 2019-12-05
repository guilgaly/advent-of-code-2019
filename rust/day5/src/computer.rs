use text_io::*;

pub type Address = i32;
pub type Memory = Vec<i32>;

pub fn execute_program(program: &Memory) -> Result<Memory, String> {
    fn get_param_value(param: &Param, memory: &Memory) -> Result<i32, String> {
        match param.mode {
            ParamMode::Immediate => Ok(param.value),
            ParamMode::Position => find_at(memory, param.value, "param"),
        }
    }

    let mut memory = program.clone();

    let mut current_address: i32 = 0;
    loop {
        let instruction = parse_instruction(&memory, current_address)?;
        println!("instruction: {:?}", instruction);
        match instruction {
            Instruction::Addition(params) => {
                let left = get_param_value(&params.left, &memory)?;
                let right = get_param_value(&params.right, &memory)?;
                let instr_res = left + right;
                memory[params.output as usize] = instr_res;
                current_address += 4;
            }
            Instruction::Multiplication(params) => {
                let left = get_param_value(&params.left, &memory)?;
                let right = get_param_value(&params.right, &memory)?;
                let instr_res = left * right;
                memory[params.output as usize] = instr_res;
                current_address += 4;
            }
            Instruction::Input(address) => {
                let input: i32 = read!();
                memory[*address.as_ref() as usize] = input;
                current_address += 2;
            }
            Instruction::Output(param) => {
                let value = get_param_value(param.as_ref(), &memory)?;
                println!("output: '{}'", value);
                current_address += 2;
            }

            Instruction::Halt => {
                break;
            }
        }
    }
    Ok(memory)
}

fn find_at(memory: &Memory, index: Address, name: &str) -> Result<i32, String> {
    memory.get(index as usize).map(|i| *i).ok_or(format!("{} out of range at index {}", name, index))
}

fn parse_instruction(memory: &Memory, address: Address) -> Result<Instruction, String> {
    fn parse_instr_code(code: i32) -> (i32, i32, i32, i32) {
        let mut digits: Vec<i32> = Vec::new();
        let mut n = code;
        while n > 9 {
            digits.push(n % 10);
            n = n / 10;
        }
        digits.push(n);
        while digits.len() < 5 {
            digits.push(0);
        }
        ((digits[0] + digits[1] * 10), digits[2], digits[3], digits[4])
    }

    fn parse_params(memory: &Memory, address: Address, mode1: i32, mode2: i32) -> Result<(Param, Param, Address), String> {
        let left_val = find_at(memory, address + 1, "left operand reference")?;
        let right_val = find_at(memory, address + 2, "right operand reference")?;
        let output_addr = find_at(memory, address + 3, "output position reference")?;

        let param_left = Param::from_code(left_val, mode1)?;
        let param_right = Param::from_code(right_val, mode2)?;

        Ok((param_left, param_right, output_addr))
    }

    let instr_code = find_at(memory, address, "opcode position")?;
    let (code, mode1, mode2, _) = parse_instr_code(instr_code);
    let opcode = Opcode::from_code(code)?;
    match opcode {
        Opcode::Add => {
            let (left, right, output) = parse_params(memory, address, mode1, mode2)?;
            Ok(Instruction::addition(left, right, output))
        }
        Opcode::Mult => {
            let (left, right, output) = parse_params(memory, address, mode1, mode2)?;
            Ok(Instruction::multiplication(left, right, output))
        }
        Opcode::In => {
            let address = find_at(memory, address + 1, "address reference")?;
            Ok(Instruction::input(address))
        }
        Opcode::Out => {
            let value = find_at(memory, address + 1, "address reference")?;
            let param = Param::from_code(value, mode1)?;
            Ok(Instruction::output(param))
        }
        Opcode::End => Ok(Instruction::Halt),
    }
}

enum Opcode {
    Add,
    Mult,
    In,
    Out,
    End,
}

impl Opcode {
    pub fn from_code(i: i32) -> Result<Opcode, String> {
        match i {
            1 => Ok(Opcode::Add),
            2 => Ok(Opcode::Mult),
            3 => Ok(Opcode::In),
            4 => Ok(Opcode::Out),
            99 => Ok(Opcode::End),
            _ => Err(format!("Illegal opcode {}", i)),
        }
    }
}

#[derive(Debug, PartialEq)]
enum Instruction {
    Addition(Box<InstructionParams>),
    Multiplication(Box<InstructionParams>),
    Input(Box<Address>),
    Output(Box<Param>),
    Halt,
}

impl Instruction {
    pub fn addition(left: Param, right: Param, output: Address) -> Instruction {
        let x = Instruction::boxed_params(left, right, output);
        Instruction::Addition(x)
    }
    pub fn multiplication(left: Param, right: Param, output: Address) -> Instruction {
        Instruction::Multiplication(Instruction::boxed_params(left, right, output))
    }
    fn boxed_params(left: Param, right: Param, output: Address) -> Box<InstructionParams> {
        Box::new(InstructionParams { left, right, output })
    }
    pub fn input(address: Address) -> Instruction {
        Instruction::Input(Box::new(address))
    }
    pub fn output(param: Param) -> Instruction {
        Instruction::Output(Box::new(param))
    }
}

#[derive(Debug, PartialEq)]
struct InstructionParams {
    left: Param,
    right: Param,
    output: Address,
}

#[derive(Debug, PartialEq)]
struct Param {
    value: i32,
    mode: ParamMode,
}

impl Param {
    fn position(address: Address) -> Param {
        Param { value: address, mode: ParamMode::Position }
    }
    fn immediate(value: i32) -> Param {
        Param { value, mode: ParamMode::Immediate }
    }
    fn from_code(value: i32, code: i32) -> Result<Param, String> {
        match code {
            0 => Ok(Param::position(value)),
            1 => Ok(Param::immediate(value)),
            _ => Err(format!("Illegal param mode code {}", code)),
        }
    }
}

#[derive(Debug, PartialEq)]
enum ParamMode {
    Position,
    Immediate,
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn parse_instruction_test() {
        let prog1 = vec![1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert_eq!(
            parse_instruction(&prog1, 0).unwrap(),
            Instruction::addition(Param::position(9), Param::position(10), 3)
        );
        assert_eq!(
            parse_instruction(&prog1, 4).unwrap(),
            Instruction::multiplication(Param::position(3), Param::position(11), 0)
        );
        assert_eq!(parse_instruction(&prog1, 8).unwrap(), Instruction::Halt);

        let prog2 = vec![0, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog2, 0).is_err());

        let prog3 = vec![100, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog3, 0).is_err());

        let prog4 = vec![1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50];
        assert!(parse_instruction(&prog4, 12).is_err());

        let prog5 = vec![1, 9, 10];
        assert!(parse_instruction(&prog5, 0).is_err());
    }

    #[test]
    fn execute_program_test() {
        assert_eq!(execute_program(&vec![1, 0, 0, 0, 99]).unwrap(), vec![2, 0, 0, 0, 99]);
        assert_eq!(execute_program(&vec![2, 3, 0, 3, 99]).unwrap(), vec![2, 3, 0, 6, 99]);
        assert_eq!(execute_program(&vec![2, 4, 4, 5, 99, 0]).unwrap(), vec![2, 4, 4, 5, 99, 9801]);
        assert_eq!(execute_program(&vec![1, 1, 1, 4, 99, 5, 6, 0, 99]).unwrap(), vec![30, 1, 1, 4, 2, 5, 6, 0, 99]);
    }
}