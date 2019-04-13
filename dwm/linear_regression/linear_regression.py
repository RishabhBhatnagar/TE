from ml_csdlo6021.linear_regression import LinearRegressor
import ml_csdlo6021
print(ml_csdlo6021.__file__)

data_set = [[34, 108, 64, 88, 99, 51], [5, 17, 11, 8, 14, 5]]
test_data = [500, 200, 100, 108]

regressor = LinearRegressor(data_set, test_size=0)
model = regressor.fit()

print("Regression Coefficient:", model.reg_coeff)
print("\nPredicting for some bill amounts: ")
for amt, tip in zip(test_data, model.predict(test_data)):
    print("Amount={} :: Tip={}".format(amt, tip))
print()
print("Resubstitution Error:", 1-model.accuracy(model.predict(data_set[0]), data_set[1]))
