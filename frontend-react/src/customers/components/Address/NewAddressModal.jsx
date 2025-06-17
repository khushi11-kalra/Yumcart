const NewAddress = ({ open, handleClose, handleAddAddress }) => {
  const handleSubmit = (values, { resetForm }) => {
    console.log("Submitted:", values);

    // ✅ Update Cart's address list
    handleAddAddress(values);

    // ✅ Reset form and close modal
    resetForm();
    handleClose();
  };

  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={style}>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
        >
          {({ errors, touched }) => (
            <Form>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Field
                    as={TextField}
                    name="streetAddress"
                    label="Street Address"
                    fullWidth
                    variant="outlined"
                    error={touched.streetAddress && Boolean(errors.streetAddress)}
                    helperText={<ErrorMessage name="streetAddress" />}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Field
                    as={TextField}
                    name="state"
                    label="State"
                    fullWidth
                    variant="outlined"
                    error={touched.state && Boolean(errors.state)}
                    helperText={<ErrorMessage name="state" />}
                  />
                </Grid>
                <Grid item xs={6}>
                  <Field
                    as={TextField}
                    name="pincode"
                    label="Pincode"
                    fullWidth
                    variant="outlined"
                    error={touched.pincode && Boolean(errors.pincode)}
                    helperText={<ErrorMessage name="pincode" />}
                  />
                </Grid>
                <Grid item xs={12}>
                  <Field
                    as={TextField}
                    name="city"
                    label="City"
                    fullWidth
                    variant="outlined"
                    error={touched.city && Boolean(errors.city)}
                    helperText={<ErrorMessage name="city" />}
                  />
                </Grid>
                <Grid item xs={12}>
                  <Button type="submit" variant="contained" color="primary">
                    Deliver Here
                  </Button>
                </Grid>
              </Grid>
            </Form>
          )}
        </Formik>
      </Box>
    </Modal>
  );
};
