fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);
    let module_masses: Vec<i64> =
        input_str
            .lines()
            .filter_map(|line| line.parse::<i64>().ok())
            .collect();

    let fuel1 = part_1::total_fuel(module_masses);
    println!("Ammount of fuel required (part 1): {}", fuel1);
}

mod part_1 {
    fn fuel_by_module(module_mass: &i64) -> i64 {
        module_mass / 3 - 2
    }

    pub fn total_fuel(module_masses: Vec<i64>) -> i64 {
        module_masses.iter().map(fuel_by_module).sum()
    }

    #[cfg(test)]
    mod test {
        use super::fuel_by_module;

        #[test]
        fn fuel_by_module_test() {
            assert_eq!(fuel_by_module(&12), 2);
            assert_eq!(fuel_by_module(&14), 2);
            assert_eq!(fuel_by_module(&1969), 654);
            assert_eq!(fuel_by_module(&100756), 33583);
        }
    }
}
