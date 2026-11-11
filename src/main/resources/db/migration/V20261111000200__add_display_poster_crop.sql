ALTER TABLE Display
    ADD COLUMN posterCropX DECIMAL(11, 10) NULL,
    ADD COLUMN posterCropY DECIMAL(11, 10) NULL,
    ADD COLUMN posterCropW DECIMAL(11, 10) NULL,
    ADD COLUMN posterCropH DECIMAL(11, 10) NULL,
    ADD CONSTRAINT chk_display_poster_crop CHECK (
        (posterCropX IS NULL AND posterCropY IS NULL AND posterCropW IS NULL AND posterCropH IS NULL)
        OR (posterCropX IS NOT NULL AND posterCropY IS NOT NULL AND posterCropW IS NOT NULL AND posterCropH IS NOT NULL
            AND posterCropX >= 0 AND posterCropY >= 0 AND posterCropW > 0 AND posterCropH > 0
            AND posterCropX + posterCropW <= 1 AND posterCropY + posterCropH <= 1)
    );
