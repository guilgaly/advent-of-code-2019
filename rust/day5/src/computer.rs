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

    fn get_2_param_values(param1: &Param, param2: &Param, memory: &Memory) -> Result<(i32, i32), String> {
        let param1_value = get_param_value(param1, &memory)?;
        let param2_value = get_param_value(param2, &memory)?;
        Ok((param1_value, param2_value))
    }

    let mut memory = program.clone();

    let mut current_address: i32 = 0;
    loop {
        let instruction = parse_instruction(&memory, current_address)?;
        match instruction {
            Instruction::Addition(params) => {
                let (left, right) = get_2_param_values(&params.left, &params.right, &memory)?;
                let instr_res = left + right;
                memory[params.output as usize] = instr_res;
                current_address += 4;
            }
            Instruction::Multiplication(params) => {
                let (left, right) = get_2_param_values(&params.left, &params.right, &memory)?;
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
            Instruction::JumpIfTrue(params) => {
                let (value, output) = get_2_param_values(&params.param1, &params.param2, &memory)?;
                current_address = if value != 0 { output } else { current_address + 3 };
            }
            Instruction::JumpIfFalse(params) => {
                let (value, output) = get_2_param_values(&params.param1, &params.param2, &memory)?;
                current_address = if value == 0 { output } else { current_address + 3 };
            }
            Instruction::LessThan(params) => {
                let (left, right) = get_2_param_values(&params.left, &params.right, &memory)?;
                let instr_res = if left < right { 1 } else { 0 };
                memory[params.output as usize] = instr_res;
                current_address += 4;
            }
            Instruction::Equals(params) => {
                let (left, right) = get_2_param_values(&params.left, &params.right, &memory)?;
                let instr_res = if left == right { 1 } else { 0 };
                memory[params.output as usize] = instr_res;
                current_address += 4;
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
        let opcode = code % 100;
        let mode1 = (code / 100) % 10;
        let mode2 = (code / 1000) % 10;
        let mode3 = (code / 10000) % 10;

        (opcode, mode1, mode2, mode3)
    }

    fn parse_params_2_to_0(memory: &Memory, address: Address, mode1: i32, mode2: i32) -> Result<(Param, Param), String> {
        let param_val1 = find_at(memory, address + 1, "param 1 reference")?;
        let param_val2 = find_at(memory, address + 2, "param 2 reference")?;

        let param1 = Param::from_code(param_val1, mode1)?;
        let param2 = Param::from_code(param_val2, mode2)?;

        Ok((param1, param2))
    }

    fn parse_params_2_to_1(memory: &Memory, address: Address, mode1: i32, mode2: i32) -> Result<(Param, Param, Address), String> {
        let left_val = find_at(memory, address + 1, "left operand reference")?;
        let right_val = find_at(memory, address + 2, "right operand reference")?;
        let output_addr = find_at(memory, address + 3, "output position reference")?;

        let param_left = Param::from_code(left_val, mode1)?;
        let param_right = Param::from_code(right_val, mode2)?;

        Ok((param_left, param_right, output_addr))
    }

    let instr_code = find_at(memory, address, "opcode position")?;
    let (code, mode1, mode2, _) = parse_instr_code(instr_code);
    let opcode = Opcode::from_code(code).map_err(|err| format!("{} at position {}", err, address))?;
    let instr = match opcode {
        Opcode::Add => {
            let (left, right, output) = parse_params_2_to_1(memory, address, mode1, mode2)?;
            Ok(Instruction::addition(left, right, output))
        }
        Opcode::Mult => {
            let (left, right, output) = parse_params_2_to_1(memory, address, mode1, mode2)?;
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
        Opcode::JmpIf => {
            let (param1, param2) = parse_params_2_to_0(memory, address, mode1, mode2)?;
            Ok(Instruction::jump_if_true(param1, param2))
        }
        Opcode::JmpIfNot => {
            let (param1, param2) = parse_params_2_to_0(memory, address, mode1, mode2)?;
            Ok(Instruction::jump_if_false(param1, param2))
        }
        Opcode::Lt => {
            let (left, right, output) = parse_params_2_to_1(memory, address, mode1, mode2)?;
            Ok(Instruction::less_than(left, right, output))
        }
        Opcode::Eq => {
            let (left, right, output) = parse_params_2_to_1(memory, address, mode1, mode2)?;
            Ok(Instruction::equals(left, right, output))
        }
        Opcode::End => Ok(Instruction::Halt),
    };

    println!("instruction ({}): {:?}", address, instr);

    instr
}

enum Opcode {
    Add,
    Mult,
    In,
    Out,
    JmpIf,
    JmpIfNot,
    Lt,
    Eq,
    End,
}

impl Opcode {
    pub fn from_code(i: i32) -> Result<Opcode, String> {
        match i {
            1 => Ok(Opcode::Add),
            2 => Ok(Opcode::Mult),
            3 => Ok(Opcode::In),
            4 => Ok(Opcode::Out),
            5 => Ok(Opcode::JmpIf),
            6 => Ok(Opcode::JmpIfNot),
            7 => Ok(Opcode::Lt),
            8 => Ok(Opcode::Eq),
            99 => Ok(Opcode::End),
            _ => Err(format!("Illegal opcode {}", i)),
        }
    }
}

#[derive(Debug, PartialEq)]
enum Instruction {
    Addition(Box<Params2To1>),
    Multiplication(Box<Params2To1>),
    Input(Box<Address>),
    Output(Box<Param>),
    JumpIfTrue(Box<Params2To0>),
    JumpIfFalse(Box<Params2To0>),
    LessThan(Box<Params2To1>),
    Equals(Box<Params2To1>),
    Halt,
}

impl Instruction {
    pub fn addition(left: Param, right: Param, output: Address) -> Instruction {
        Instruction::Addition(Instruction::boxed_params(left, right, output))
    }
    pub fn multiplication(left: Param, right: Param, output: Address) -> Instruction {
        Instruction::Multiplication(Instruction::boxed_params(left, right, output))
    }
    pub fn input(address: Address) -> Instruction {
        Instruction::Input(Box::new(address))
    }
    pub fn output(param: Param) -> Instruction {
        Instruction::Output(Box::new(param))
    }
    pub fn jump_if_true(param1: Param, param2: Param) -> Instruction {
        Instruction::JumpIfTrue(Box::new(Params2To0 { param1, param2 }))
    }
    pub fn jump_if_false(param1: Param, param2: Param) -> Instruction {
        Instruction::JumpIfFalse(Box::new(Params2To0 { param1, param2 }))
    }
    pub fn less_than(left: Param, right: Param, output: Address) -> Instruction {
        Instruction::LessThan(Instruction::boxed_params(left, right, output))
    }
    pub fn equals(left: Param, right: Param, output: Address) -> Instruction {
        Instruction::Equals(Instruction::boxed_params(left, right, output))
    }

    fn boxed_params(left: Param, right: Param, output: Address) -> Box<Params2To1> {
        Box::new(Params2To1 { left, right, output })
    }
}

#[derive(Debug, PartialEq)]
struct Params2To0 {
    param1: Param,
    param2: Param,
}

#[derive(Debug, PartialEq)]
struct Params2To1 {
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