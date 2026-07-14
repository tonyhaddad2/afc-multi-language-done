"""
be lahza Blender production scene generator.

Run inside Blender:
  blender --background --python animation_pipeline/blender_scene_generator.py -- cpr

This creates a clean mannequin-style emergency training scene template with:
- soft medical studio lighting
- two clay mannequins
- camera and floor setup
- timeline markers for the maneuver

A real 3D animator should refine rigging and keyframes manually, then export MP4
using the names in app/src/main/res/raw/.
"""

import sys
import math
import bpy
from mathutils import Vector

MANEUVERS = {
    "cpr": ["rescuer kneels", "hands on chest", "compressions loop"],
    "choking": ["rescuer behind", "fist above navel", "inward upward thrust"],
    "bleeding": ["training pad", "two hand direct pressure", "hold pressure"],
    "burns": ["cool water", "remove tight items", "loose cover"],
    "seizure": ["clear hazards", "protect head", "do not restrain"],
    "unconscious": ["check breathing", "roll to side", "monitor airway"],
    "allergic": ["trainer injector", "outer thigh", "call help"],
    "fire": ["stay low", "move to exit", "signal help"]
}

def arg_maneuver():
    if "--" in sys.argv:
        args = sys.argv[sys.argv.index("--")+1:]
        if args:
            return args[0]
    return "cpr"

def clear():
    bpy.ops.object.select_all(action='SELECT')
    bpy.ops.object.delete()

def material(name, color):
    mat = bpy.data.materials.new(name)
    mat.use_nodes = True
    mat.node_tree.nodes["Principled BSDF"].inputs["Base Color"].default_value = color
    mat.node_tree.nodes["Principled BSDF"].inputs["Roughness"].default_value = 0.72
    return mat

def capsule(name, radius, depth, loc, rot=(0,0,0), mat=None):
    bpy.ops.mesh.primitive_uv_sphere_add(segments=32, ring_count=16, radius=radius, location=loc)
    sph = bpy.context.object
    sph.name = name + "_joint"
    if mat: sph.data.materials.append(mat)
    bpy.ops.mesh.primitive_cylinder_add(vertices=32, radius=radius, depth=depth, location=loc, rotation=rot)
    obj = bpy.context.object
    obj.name = name
    if mat: obj.data.materials.append(mat)
    return obj

def mannequin(prefix, x, y, z, scale, mat):
    # Simple block-out template; animator should replace with final rig.
    parts = []
    bpy.ops.mesh.primitive_uv_sphere_add(segments=48, ring_count=24, radius=0.22*scale, location=(x,y,z+1.65*scale))
    head = bpy.context.object; head.name = prefix+"_head"; head.data.materials.append(mat); parts.append(head)
    bpy.ops.mesh.primitive_cube_add(size=1, location=(x,y,z+1.08*scale))
    torso = bpy.context.object; torso.name = prefix+"_torso"; torso.scale=(0.34*scale,0.22*scale,0.55*scale); torso.data.materials.append(mat); parts.append(torso)
    for side in [-1,1]:
        parts.append(capsule(prefix+("_upper_arm_L" if side<0 else "_upper_arm_R"),0.065*scale,0.62*scale,(x+side*.34*scale,y,z+1.20*scale),(0,math.radians(70),0),mat))
        parts.append(capsule(prefix+("_forearm_L" if side<0 else "_forearm_R"),0.06*scale,0.55*scale,(x+side*.56*scale,y,z+.92*scale),(0,math.radians(70),0),mat))
        parts.append(capsule(prefix+("_thigh_L" if side<0 else "_thigh_R"),0.09*scale,0.72*scale,(x+side*.14*scale,y,z+.48*scale),(0,0,0),mat))
        parts.append(capsule(prefix+("_shin_L" if side<0 else "_shin_R"),0.08*scale,0.72*scale,(x+side*.14*scale,y,z-.20*scale),(0,0,0),mat))
    return parts

def setup_scene(maneuver):
    clear()
    skin = material("warm_clay_mannequin", (0.72,0.52,0.32,1))
    floor_mat = material("medical_cyan_floor", (0.52,0.93,0.95,1))
    red = material("emergency_red_marker", (0.92,0.05,0.08,1))

    bpy.ops.mesh.primitive_plane_add(size=8, location=(0,0,0))
    floor = bpy.context.object; floor.name = "medical_floor"; floor.data.materials.append(floor_mat)

    mannequin("rescuer", -0.8, 0, 0, 1.0, skin)
    mannequin("patient", 0.9, 0, 0, 1.0, skin)

    bpy.ops.object.light_add(type='AREA', location=(0,-3,5))
    light = bpy.context.object; light.name = "large_softbox"; light.data.energy = 600; light.data.size = 5
    bpy.ops.object.camera_add(location=(0,-5,2.1), rotation=(math.radians(68),0,0))
    bpy.context.scene.camera = bpy.context.object

    bpy.context.scene.frame_start = 1
    bpy.context.scene.frame_end = 144
    bpy.context.scene.render.fps = 24
    bpy.context.scene.render.resolution_x = 1920
    bpy.context.scene.render.resolution_y = 1080

    for i, label in enumerate(MANEUVERS.get(maneuver, ["pose 1","pose 2","pose 3"])):
        marker = bpy.context.scene.timeline_markers.new(label, frame=1+i*48)
        marker.camera = bpy.context.scene.camera

    bpy.ops.wm.save_as_mainfile(filepath=f"be_lahza_{maneuver}_template.blend")

if __name__ == "__main__":
    setup_scene(arg_maneuver())
