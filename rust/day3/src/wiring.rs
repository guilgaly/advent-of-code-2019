use std::cmp::{max, min};

use regex::Regex;

use lazy_static::lazy_static;
use std::collections::HashSet;

pub fn find_shortest_path(w1: &Wire, w2: &Wire)-> Option<i32> {
    let paths: Vec<i32> = find_intersections(w1, w2).into_iter()
        .map(|(_, path1, path2)| path1 + path2)
        .collect();
    paths.into_iter().min()
}

pub fn find_shortest_distance(w1: &Wire, w2: &Wire) -> Option<i32> {
    let distances: Vec<i32> = find_intersections(w1, w2).into_iter()
            .map(|(point, _, _)| point.x.abs() + point.y.abs())
            .collect();
    distances.into_iter().min()
}

fn find_intersections(w1: &Wire, w2: &Wire) -> HashSet<(Point, i32, i32)> {
    fn between(a1: &i32, a2: &i32, b: &i32) -> bool {
        min(a1, a2) < b && b < max(a1, a2)
    }

    let mut res: HashSet<(Point, i32, i32)> = HashSet::new();
    let mut path1: i32 = 0;
    for seg1 in w1 {
        let mut path2: i32 = 0;
        for seg2 in w2 {
            match (seg1, seg2) {
                (Segment::H { x1, x2, y }, Segment::V { x, y1, y2 }) => {
                    if between(x1, x2, x) && between(y1, y2, y) {
                        let path1_intersect = path1 + (x - x1).abs();
                        let path2_intersect = path2 + (y - y1).abs();
                        res.insert((Point { x: *x, y: *y }, path1_intersect, path2_intersect));
                    }
                }
                (Segment::V { x, y1, y2 }, Segment::H { x1, x2, y }) => {
                    if between(x1, x2, x) && between(y1, y2, y) {
                        let path1_intersect = path1 + (y - y1).abs();
                        let path2_intersect = path2 + (x - x1).abs();
                        res.insert((Point { x: *x, y: *y }, path1_intersect, path2_intersect));
                    }
                }
                _ => ()
            };
            path2 += seg2.length();
        }
        path1 += seg1.length();
    };
    res
}

pub fn parse_wire(s: &str) -> Result<Wire, String> {
    let maybe_moves: Result<Vec<Move>, String> = s.split(',').map(|mve| Move::parse(mve)).collect();
    maybe_moves.map(|moves| {
        let mut res: Wire = Vec::new();
        let mut curr_pt = Point { x: 0, y: 0 };
        for mve in moves.into_iter() {
            let seg: Segment;
            match mve {
                Move::V(length) => {
                    let new_point = Point { x: curr_pt.x, y: curr_pt.y + length };
                    seg = Segment::V { x: curr_pt.x, y1: curr_pt.y, y2: new_point.y };
                    curr_pt = new_point;
                }
                Move::H(length) => {
                    let new_point = Point { x: curr_pt.x + length, y: curr_pt.y };
                    seg = Segment::H { x1: curr_pt.x, x2: new_point.x, y: curr_pt.y };
                    curr_pt = new_point;
                }
            };
            res.push(seg);
        }
        res
    })
}

#[derive(PartialEq, Eq, Hash, Debug)]
pub struct Point {
    x: i32,
    y: i32,
}

#[derive(PartialEq, Debug)]
pub enum Segment {
    H { x1: i32, x2: i32, y: i32 },
    V { x: i32, y1: i32, y2: i32 },
}

impl Segment {
    fn length(&self) -> i32 {
        match self {
            Segment::H { x1, x2, y: _ } => (x2 - x1).abs(),
            Segment::V { x: _, y1, y2 } => (y2 - y1).abs(),
        }
    }
}

pub type Wire = Vec<Segment>;

#[derive(PartialEq, Debug)]
enum Move {
    H(i32),
    V(i32),
}

impl Move {
    fn parse(s: &str) -> Result<Move, String> {
        lazy_static! {
            static ref RE: Regex = Regex::new(r"(?P<code>[URDL])(?P<length>\d+)").unwrap();
        }
        let cap = RE.captures(s).ok_or(format!("cannot parse move {}", s))?;
        let code = cap.name("code")
            .map(|code| code.as_str())
            .ok_or(format!("cannot parse code in move {}", s))?;
        let length = cap.name("length")
            .and_then(|length| length.as_str().parse::<i32>().ok())
            .ok_or(format!("cannot parse length in move {}", s))?;

        match code {
            "U" => Ok(Move::V(length)),
            "R" => Ok(Move::H(length)),
            "D" => Ok(Move::V(-length)),
            "L" => Ok(Move::H(-length)),
            _ => Err(format!("cannot parse code {}", code)),
        }
    }
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn move_parse_test() {
        assert_eq!(Move::parse(&"U25").unwrap(), Move::V(25));
        assert_eq!(Move::parse(&"R1").unwrap(), Move::H(1));
        assert_eq!(Move::parse(&"D9426").unwrap(), Move::V(-9426));
        assert_eq!(Move::parse(&"L75").unwrap(), Move::H(-75));
        assert!(Move::parse(&"S25").is_err());
    }

    #[test]
    fn parse_wire_test() {
        assert_eq!(
            parse_wire("R75,D30,R83,U83,L12").unwrap(),
            vec!(Segment::H { x1: 0, x2: 75, y: 0 }, Segment::V { x: 75, y1: 0, y2: -30 },
                 Segment::H { x1: 75, x2: 158, y: -30 }, Segment::V { x: 158, y1: -30, y2: 53 },
                 Segment::H { x1: 158, x2: 146, y: 53 })
        );
    }

    #[test]
    fn find_intersections_test() {
        let w1 = parse_wire("R8,U5,L5,D3").unwrap();
        let w2 = parse_wire("U7,R6,D4,L4").unwrap();
        let expected: HashSet<(Point, i32, i32)> = vec![(Point { x: 3, y: 3 }, 20, 20), (Point { x: 6, y: 5 }, 15, 15)].into_iter().collect();
        assert_eq!(find_intersections(&w1, &w2), expected)
    }
}
