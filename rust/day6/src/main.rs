use regex::Regex;

use lazy_static::lazy_static;

fn main() {
    let input_bytes = include_bytes!("input");
    let input_str = String::from_utf8_lossy(input_bytes);

    let map_tree = build_map_tree(input_str.as_ref());

    println!("All orbits count: {}", map_tree.unwrap().total_orbits_count());
}

struct MapNode {
    name: String,
    orbits: Vec<MapNode>,
}

impl MapNode {
    fn all_orbits(&self) -> Vec<&MapNode> {
        let mut res: Vec<&MapNode> = Vec::new();
        res.extend(self.orbits.iter());
        res.extend(self.orbits.iter().flat_map(|orbit| orbit.all_orbits()));
        res
    }
    fn total_orbits_count(&self) -> usize {
        let indirect_count: usize = self.orbits.iter().map(|orbit| orbit.total_orbits_count()).sum();
        self.all_orbits().len() + indirect_count
    }
}

fn build_map_tree(orbits_str: &str) -> Result<MapNode, String> {
    parse_orbits(orbits_str).map(|orbits_list|
        build_map_node("COM", &orbits_list)
    )
}

fn build_map_node(name: &str, orbits_list: &Vec<(String, String)>) -> MapNode {
    let orbits: Vec<MapNode> =
        orbits_list.iter()
            .filter_map(|(center, orbiter)|
                if center == name {
                    Some(build_map_node(orbiter, orbits_list))
                } else {
                    None
                }
            )
            .collect();
    MapNode { name: name.to_string(), orbits }
}

fn parse_orbits(orbits_map: &str) -> Result<Vec<(String, String)>, String> {
    lazy_static! {
            static ref RE: Regex = Regex::new(r"(?P<center>[A-Z0-9]{3})\)(?P<orbiter>[A-Z0-9]{3})").unwrap();
    }

    orbits_map
        .lines()
        .map(|line| {
            let cap = RE.captures(line).ok_or(format!("cannot parse map line {}", line))?;
            let center = cap.name("center")
                .map(|code| code.as_str().to_string())
                .ok_or(format!("cannot parse center in map line {}", line))?;
            let orbiter = cap.name("orbiter")
                .map(|code| code.as_str().to_string())
                .ok_or(format!("cannot parse orbiter in map line {}", line))?;
            Ok((center, orbiter))
        })
        .collect()
}
