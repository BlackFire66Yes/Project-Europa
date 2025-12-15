package com.europa.europamod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.EnumMap;
import java.util.Map;

public class CrystalBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);

    // Храним формы для каждого варианта и направления
    private final Map<Integer, Map<Direction, VoxelShape>> shapesByVariant = new java.util.HashMap<>();

    public CrystalBlock(Properties properties, Map<Integer, double[][]> variantBoxes) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.DOWN)
                .setValue(WATERLOGGED, false)
                .setValue(VARIANT, 0));
        buildAllShapes(variantBoxes);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        RandomSource random = context.getLevel().getRandom();

        return defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(VARIANT, random.nextInt(3));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int variant = state.getValue(VARIANT);
        Direction facing = state.getValue(FACING);

        Map<Direction, VoxelShape> variantShapes = shapesByVariant.get(variant);
        if (variantShapes != null) {
            return variantShapes.getOrDefault(facing, Shapes.empty());
        }
        return Shapes.empty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.getBlock() instanceof CrystalBlock || super.skipRendering(state, adjacentState, direction);
    }

    public BlockState getStateForWorldGen(RandomSource random) {
        return defaultBlockState()
                .setValue(FACING, Direction.values()[random.nextInt(Direction.values().length)])
                .setValue(VARIANT, random.nextInt(3));
    }

    private void buildAllShapes(Map<Integer, double[][]> variantBoxes) {
        for (Map.Entry<Integer, double[][]> entry : variantBoxes.entrySet()) {
            int variant = entry.getKey();
            double[][] boxes = entry.getValue();

            Map<Direction, VoxelShape> variantShapes = new EnumMap<>(Direction.class);
            buildShapesForVariant(variantShapes, boxes);
            shapesByVariant.put(variant, variantShapes);
        }
    }

    private void buildShapesForVariant(Map<Direction, VoxelShape> shapes, double[][] boxes) {
        for (Direction dir : Direction.values()) {
            int[] rot = rotationForFacing(dir);
            VoxelShape s = Shapes.empty();
            for (double[] b : boxes) {
                double[] rb = rotateBox(b, rot[0], rot[1]);
                s = Shapes.or(s, Block.box(rb[0], rb[1], rb[2], rb[3], rb[4], rb[5]));
            }
            shapes.put(dir, s);
        }
    }

    private int[] rotationForFacing(Direction facing) {
        return switch (facing) {
            case DOWN -> new int[]{180, 0};
            case UP -> new int[]{0, 0};
            case NORTH -> new int[]{270, 0};
            case SOUTH -> new int[]{90, 0};
            case WEST -> new int[]{90, 270};
            case EAST -> new int[]{90, 90};
        };
    }

    private double[] rotateBox(double[] box, int xDeg, int yDeg) {
        double minX = box[0], minY = box[1], minZ = box[2];
        double maxX = box[3], maxY = box[4], maxZ = box[5];

        double cx = 8.0, cy = 8.0, cz = 8.0;

        double[][] corners = new double[][] {
                {minX, minY, minZ}, {minX, minY, maxZ}, {minX, maxY, minZ}, {minX, maxY, maxZ},
                {maxX, minY, minZ}, {maxX, minY, maxZ}, {maxX, maxY, minZ}, {maxX, maxY, maxZ}
        };

        double minRx = Double.POSITIVE_INFINITY, minRy = Double.POSITIVE_INFINITY, minRz = Double.POSITIVE_INFINITY;
        double maxRx = Double.NEGATIVE_INFINITY, maxRy = Double.NEGATIVE_INFINITY, maxRz = Double.NEGATIVE_INFINITY;

        double sinX = Math.sin(Math.toRadians(xDeg)), cosX = Math.cos(Math.toRadians(xDeg));
        double sinY = Math.sin(Math.toRadians(yDeg)), cosY = Math.cos(Math.toRadians(yDeg));

        for (double[] c : corners) {
            double x = c[0] - cx;
            double y = c[1] - cy;
            double z = c[2] - cz;

            // Поворот относительно оси X
            double y1 = y * cosX - z * sinX;
            double z1 = y * sinX + z * cosX;
            double x1 = x;

            // Поворот относительно оси Y
            double x2 = x1 * cosY + z1 * sinY;
            double z2 = -x1 * sinY + z1 * cosY;
            double y2 = y1;

            double rx = x2 + cx;
            double ry = y2 + cy;
            double rz = z2 + cz;

            if (rx < minRx) minRx = rx;
            if (ry < minRy) minRy = ry;
            if (rz < minRz) minRz = rz;
            if (rx > maxRx) maxRx = rx;
            if (ry > maxRy) maxRy = ry;
            if (rz > maxRz) maxRz = rz;
        }

        // Ограничиваем значения от 0 до 16
        minRx = Math.max(0.0, Math.min(16.0, minRx));
        minRy = Math.max(0.0, Math.min(16.0, minRy));
        minRz = Math.max(0.0, Math.min(16.0, minRz));
        maxRx = Math.max(0.0, Math.min(16.0, maxRx));
        maxRy = Math.max(0.0, Math.min(16.0, maxRy));
        maxRz = Math.max(0.0, Math.min(16.0, maxRz));

        return new double[]{minRx, minRy, minRz, maxRx, maxRy, maxRz};
    }
}
