fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);
    let module_masses: Vec<i64> =
        input_str
            .lines()
            .filter_map(|line| line.parse::<i64>().ok())
            .collect();

    let fuel1 = part_1::total_fuel(&module_masses);
    println!("Ammount of fuel required (part 1): {}", fuel1);

    let fuel2 = part_2::total_fuel(&module_masses);
    println!("Ammount of fuel required (part 2): {}", fuel2);
}

mod part_1 {
    pub fn total_fuel(module_masses: &Vec<i64>) -> i64 {
        module_masses.iter().map(fuel_by_module).sum()
    }

    fn fuel_by_module(module_mass: &i64) -> i64 {
        module_mass / 3 - 2
    }

    #[cfg(test)]
    mod test {
        use super::*;

        #[test]
        fn fuel_by_module_test() {
            assert_eq!(fuel_by_module(&12), 2);
            assert_eq!(fuel_by_module(&14), 2);
            assert_eq!(fuel_by_module(&1969), 654);
            assert_eq!(fuel_by_module(&100756), 33583);
        }
    }
}

mod part_2 {
    pub fn total_fuel(module_masses: &Vec<i64>) -> i64 {
        module_masses.iter().map(fuel_by_mass).sum()
    }

    fn fuel_by_mass(mass: &i64) -> i64 {
        let base_fuel = mass / 3 - 2;
        if base_fuel <= 0 {
            0
        } else {
            base_fuel + fuel_by_mass(&base_fuel)
        }
    }

    #[cfg(test)]
    mod test {
        use super::*;

        #[test]
        fn fuel_by_mass_test() {
            assert_eq!(fuel_by_mass(&14), 2);
            assert_eq!(fuel_by_mass(&1969), 966);
            assert_eq!(fuel_by_mass(&100756), 50346);
        }
    }
}
