package ecs.Components;

import ecs.Particle;

import java.util.ArrayList;
import java.util.HashMap;

public class Particles extends Component{
    public final HashMap<Long, Particle> particles;
    public Particles(){
        particles = new HashMap<>();
    }
}
